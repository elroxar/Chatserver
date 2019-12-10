package client.gui;

import client.ChatClient;
import client.gui.chatviewer.ChatViewer;
import client.gui.signin.SignIn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUI frame
 *
 * @author Stefan Christian Kohlmeier
 * @version 05.12.2019
 */
public class GUI extends JFrame implements ActionListener {

    private ChatClient client;

    private JButton signOut;
    private ChatViewer chatViewer;

    /**
     * constructor
     */
    public GUI() {
        super("Chat");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        client = new ChatClient(this);
        setContentPane(new SignIn(client));
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * sets up the chat viewer
     *
     * @param chatClient chat client
     */
    public void signIn(ChatClient chatClient) {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        menuBar.add(signOut = new JButton("sign out"));
        signOut.addActionListener(this);
        setContentPane(chatViewer = new ChatViewer(chatClient));
        repaint();
        revalidate();
    }

    /**
     * gets the chat viewer
     *
     * @return
     */
    public ChatViewer getChatViewer() {
        return chatViewer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signOut) {
            chatViewer.signOut();
            setJMenuBar(null);
            signOut = null;
            setContentPane(new SignIn(client));
            repaint();
            revalidate();
        }
    }
}

