package client.gui.chatviewer;

import client.ChatClient;
import libary.datastructure.linear.list.List;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * represents the chat viewer
 *
 * @author Stefan Christian Kohlmeier
 * @version 05.12.2019
 */
public class ChatViewer extends JSplitPane implements PropertyChangeListener {

    private ChatClient chatClient;

    private ChatList chatList;
    private List<Chat> chats;
    private Chat currentChat;

    private JScrollPane right;

    /**
     * constructor
     *
     * @param chatClient chat client
     */
    public ChatViewer(ChatClient chatClient) {
        super(JSplitPane.HORIZONTAL_SPLIT);
        this.chatClient = chatClient;
        chats = new List<>();
        setResizeWeight(0.2);
        addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, this);
        JPanel leftWrapper = new JPanel(new BorderLayout());
        JScrollPane left = new JScrollPane();
        leftWrapper.add(left, BorderLayout.CENTER);
        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.add(right = new JScrollPane(), BorderLayout.CENTER);
        rightWrapper.add(new SendBar(this), BorderLayout.SOUTH);
        chatList = new ChatList();
        chatList.setLayout(new BoxLayout(chatList, BoxLayout.Y_AXIS));
        left.setViewportView(chatList);
        add(leftWrapper, JSplitPane.LEFT);
        add(rightWrapper, JSplitPane.RIGHT);
        addChat(new Chat(this, "Hallo ich bin dumm", true));
    }

    /**
     * adds a chat to the chat viewer
     *
     * @param chat chat
     */
    public void addChat(Chat chat) {
        chatList.add(chat.getChatSelection());
        chats.append(chat);
        JButton button = chat.getChatSelection();
        button.setMaximumSize(new Dimension(getDividerLocation(), button.getFont().getSize() + 2));
        button.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
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
            chatClient.sendGroupMessage(message);
        else
            chatClient.sendChatMessage(message);

    }

    /**
     * signs out the client
     */
    public void signOut() {
        chatClient.signOut();
    }

    /**
     * sends a group message
     *
     * @param group     group
     * @param sender    sender
     * @param timestamp timestamp
     * @param message   message
     */
    public void sendGroupMessage(String group, String sender, String timestamp, String message) {
        chats.toFirst();
        while (chats.hasAccess()) {
            Chat chat = chats.getContent();
            if (chat.isGroup() && chat.getChatSelection().getText().equals(group))
                chat.addMessage(sender, timestamp, message);
            chats.next();
        }
    }

    /**
     * sends a chat message
     *
     * @param receiver  receiver
     * @param sender    sender
     * @param timestamp timestamp
     * @param message   message
     */
    public void sendChatMessage(String receiver, String sender, String timestamp, String message) {
        chats.toFirst();
        while (chats.hasAccess()) {
            Chat chat = chats.getContent();
            if (!chat.isGroup() && chat.getChatSelection().getText().equals(receiver))
                chat.addMessage(sender, timestamp, message);
            chats.next();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == this) {
            for (Component c : chatList.getComponents())
                c.setMaximumSize(new Dimension((int) evt.getNewValue(), c.getHeight()));
        }
    }
}
