package client.gui.signin;

import client.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * represents the login screen
 *
 * @author Stefan Christian Kohlmeier
 * @version 05.12.2019
 */
public class SignIn extends JPanel implements ActionListener, FocusListener, MouseListener {

    private ChatClient client;

    private JTextField name;
    private JPasswordField password;
    private JButton showPassword;
    private JButton signIn;

    public SignIn(ChatClient client) {
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        this.client = client;
        c.gridx = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = GridBagConstraints.RELATIVE;
        c.insets.left = 100;
        c.insets.right = 100;
        c.weightx = 1;
        add(name = new JTextField("name"), c);
        add(password = new JPasswordField("password"), c);
        password.setEchoChar((char) 0);
        password.addFocusListener(this);
        c.gridwidth = 1;
        c.weightx = 0.9;
        c.insets.right = 0;
        add(signIn = new JButton("sign in"), c);
        signIn.addActionListener(this);
        c.weightx = 0.1;
        c.gridy = 2;
        c.gridx = 1;
        c.insets.right = 100;
        c.insets.left = 0;
        add(showPassword = new JButton("show password"), c);
        showPassword.addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signIn) {
            client.signIn(name.getText(), new String(password.getPassword()));
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (e.getSource() == password && !password.echoCharIsSet())
            password.setEchoChar('*');
    }

    @Override
    public void focusLost(FocusEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == showPassword)
            password.setEchoChar((char) 0);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() == showPassword)
            password.setEchoChar('*');
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
