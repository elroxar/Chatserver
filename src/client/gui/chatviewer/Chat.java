package client.gui.chatviewer;

import client.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * class containing the necessary objects to present one chat
 *
 * @author Stefan Christian Kohlmeier
 * @version 05.12.2019
 */
public class Chat implements ActionListener, MouseListener {

    private ChatClient client;
    private ChatViewer chatViewer;

    private JPopupMenu popupMenu;
    private JMenuItem leaveChat;
    private JMenuItem createChat;
    private JMenuItem createGroup;
    private JMenuItem joinGroup;

    private JButton chatSelection;
    private JTextArea chat;
    private boolean isGroup;

    /**
     * constructor
     *
     * @param client     client
     * @param chatViewer chat viewer
     * @param chatName   chat name
     * @param isGroup    <code>true</code>, if the chat is a group, otherwise <code>false</code>
     */
    public Chat(ChatClient client, ChatViewer chatViewer, String chatName, boolean isGroup) {
        this.chatViewer = chatViewer;
        this.isGroup = isGroup;
        this.client = client;
        popupMenu = new JPopupMenu();
        popupMenu.add(leaveChat = new JMenuItem("leave chat"));
        leaveChat.addActionListener(this);
        popupMenu.add(createChat = new JMenuItem("create chat"));
        createChat.addActionListener(this);
        popupMenu.add(createGroup = new JMenuItem("create group"));
        createGroup.addActionListener(this);
        popupMenu.add(joinGroup = new JMenuItem("join group"));
        chatSelection = new JButton(chatName);
        chatSelection.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        chatSelection.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        chatSelection.addActionListener(this);
        chat = new JTextArea();
        chat.setEditable(false);
        chat.setLineWrap(true);
        chat.setWrapStyleWord(true);
        chatSelection.addMouseListener(this);
        chatSelection.addMouseListener(this);
    }

    /**
     * returns, if the chat is a group
     *
     * @return <code>true</code>, if the chat is a group, otherwise <code>false</code>
     */
    public boolean isGroup() {
        return isGroup;
    }

    /**
     * gets the text selection button
     *
     * @return text selection button
     */
    public JButton getChatSelection() {
        return chatSelection;
    }

    /**
     * gets the chat field
     *
     * @return chat field
     */
    public JTextArea getChat() {
        return chat;
    }

    public void addMessage(String timestamp, String sender, String message) {
        chat.append("<" + timestamp + "> " + sender + ": " + message + "\n");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chatSelection) {
            chatViewer.setCurrentChat(this);
        } else if (e.getSource() == createChat) {
            String s = (String) JOptionPane.showInputDialog(chatViewer, null, "set chat partner",
                    JOptionPane.PLAIN_MESSAGE, null, null, null);
            chatViewer.addChat(new Chat(client, chatViewer, s, false));
        } else if (e.getSource() == createGroup) {
            String s = (String) JOptionPane.showInputDialog(chatViewer, null, "set group name",
                    JOptionPane.PLAIN_MESSAGE, null, null, null);
            client.createGroup(s);
        } else if (e.getSource() == joinGroup) {
            String s = (String) JOptionPane.showInputDialog(chatViewer, null, "join group",
                    JOptionPane.PLAIN_MESSAGE, null, null, null);
            client.joinGroup(s);
        } else if (e.getSource() == leaveChat) {
            chatViewer.removeChat(this);
            if (isGroup) client.leaveGroup(chatSelection.getText());
            else client.leaveChat(chatSelection.getText());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == chatSelection && e.isPopupTrigger())
            popupMenu.show(chatSelection, e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == chatSelection && e.isPopupTrigger())
            popupMenu.show(chatSelection, e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() == chatSelection && e.isPopupTrigger())
            popupMenu.show(chatSelection, e.getX(), e.getY());
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
