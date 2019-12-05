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
        setContentPane(new SignIn(this));
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new GUI();
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
        add(chatViewer = new ChatViewer(chatClient));
    }

    /**
     * gets the chat viewer
     *
     * @return
     */
    public ChatViewer getChatViewer() {
        return chatViewer;
    }

    /**
     * returns to the sign in screen
     */
    public void signOut() {
        chatViewer.signOut();
        setJMenuBar(null);
        signOut = null;
        setContentPane(new SignIn(this));
        repaint();
        revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signOut)
            signOut();
    }
}
