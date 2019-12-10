package client.gui.chatviewer;

import client.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

/**
 * represents the chat viewer
 *
 * @author Stefan Christian Kohlmeier
 * @version 05.12.2019
 */
public class ChatViewer extends JSplitPane implements PropertyChangeListener {

    private ChatClient client;

    private ChatList chatList;
    private List<Chat> chats;
    private Chat currentChat;

    private JScrollPane right;

    /**
     * constructor
     *
     * @param client client
     */
    public ChatViewer(ChatClient client) {
        super(JSplitPane.HORIZONTAL_SPLIT);
        this.client = client;
        chats = new LinkedList<>();
        setResizeWeight(0.2);
        addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, this);
        JPanel leftWrapper = new JPanel(new BorderLayout());
        JScrollPane left = new JScrollPane();
        leftWrapper.add(left, BorderLayout.CENTER);
        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.add(right = new JScrollPane(), BorderLayout.CENTER);
        rightWrapper.add(new SendBar(this), BorderLayout.SOUTH);
        chatList = new ChatList(client, this);
        chatList.setLayout(new BoxLayout(chatList, BoxLayout.Y_AXIS));
        left.setViewportView(chatList);
        add(leftWrapper, JSplitPane.LEFT);
        add(rightWrapper, JSplitPane.RIGHT);
    }

    /**
     * removes a chat from the chat viewer
     *
     * @param chat chat
     */
    public void removeChat(Chat chat) {
        System.out.println("remove " + chat);
        chats.remove(chat);
        chatList.remove(chat.getChatSelection());
        if (currentChat == chat)
            currentChat = null;
        revalidate();
        repaint();
    }

    /**
     * adds a chat to the chat viewer
     *
     * @param chat chat
     */
    public void addChat(Chat chat) {
        chatList.add(chat.getChatSelection());
        chats.add(chat);
        JButton button = chat.getChatSelection();
        button.setMaximumSize(new Dimension(getDividerLocation(), button.getFont().getSize() + 2));
        button.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        revalidate();
        repaint();
    }

    /**
     * selects the current chat
     *
     * @param chat chat
     */
    public void setCurrentChat(Chat chat) {
        currentChat = chat;
        right.setViewportView(chat.getChat());
    }

    /**
     * sends a message to the chat server
     *
     * @param message message
     */
    public void send(String message) {
        if (currentChat == null) return;
        if (currentChat.isGroup())
            client.sendGroupMessage(getCurrentChatName(), message);
        else
            client.sendChatMessage(getCurrentChatName(), message);

    }

    /**
     * gets the current chat name
     *
     * @return current chat name
     */
    private String getCurrentChatName() {
        return currentChat.getChatSelection().getText();
    }

    /**
     * signs out the client
     */
    public void signOut() {
        client.signOut();
    }

    /**
     * gets a group
     *
     * @param group group name
     * @return the group, if no group exists, which matches the name, return <code>null</code>
     */
    public Chat getGroup(String group) {
        for (Chat c : chats)
            if (c.isGroup() && c.getChatSelection().getText().equals(group))
                return c;
        return null;
    }

    /**
     * gets a chat
     *
     * @param chat chat name
     * @return the chat, if no chat exists, which matches the name, return <code>null</code>
     */
    public Chat getChat(String chat) {
        for (Chat c : chats)
            if (!c.isGroup() && c.getChatSelection().getText().equals(chat))
                return c;
        return null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == this) {
            for (Component c : chatList.getComponents())
                c.setMaximumSize(new Dimension((int) evt.getNewValue(), c.getHeight()));
        }
    }
}
