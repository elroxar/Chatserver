package client.gui.chatviewer;

import client.ChatClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * class representing the chat selection list
 *
 * @author Stefan Christian Kohlmeier
 * @version 05.12.2019
 */
public class ChatList extends JPanel implements ActionListener, MouseListener {

    private ChatClient client;
    private ChatViewer chatViewer;

    private JPopupMenu popupMenu;
    private JMenuItem createChat;
    private JMenuItem createGroup;
    private JMenuItem joinGroup;

    /**
     * constructor
     *
     * @param client     client
     * @param chatViewer chat viewer
     */
    public ChatList(ChatClient client, ChatViewer chatViewer) {
        this.client = client;
        this.chatViewer = chatViewer;
        popupMenu = new JPopupMenu();
        popupMenu.add(createChat = new JMenuItem("create chat"));
        createChat.addActionListener(this);
        popupMenu.add(createGroup = new JMenuItem("create group"));
        createGroup.addActionListener(this);
        popupMenu.add(joinGroup = new JMenuItem("join group"));
        joinGroup.addActionListener(this);
        addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createChat) {
            String s = (String) JOptionPane.showInputDialog(this, null, "set chat partner",
                    JOptionPane.PLAIN_MESSAGE, null, null, null);
            chatViewer.addChat(new Chat(client, chatViewer, s, false));
        } else if (e.getSource() == createGroup) {
            String s = (String) JOptionPane.showInputDialog(this, null, "set group name",
                    JOptionPane.PLAIN_MESSAGE, null, null, null);
            client.createGroup(s);
        } else if (e.getSource() == joinGroup) {
            String s = (String) JOptionPane.showInputDialog(this, null, "join group",
                    JOptionPane.PLAIN_MESSAGE, null, null, null);
            client.joinGroup(s);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isPopupTrigger())
            popupMenu.show(this, e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger())
            popupMenu.show(this, e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
            popupMenu.show(this, e.getX(), e.getY());
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
