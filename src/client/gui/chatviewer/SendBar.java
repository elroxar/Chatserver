package client.gui.chatviewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * class representing the send bar
 *
 * @author Stefan Christian Kohlmeier
 * @version 05.12.2019
 */
public class SendBar extends JPanel implements ActionListener {

    private ChatViewer chatViewer;

    private JButton send;
    private JTextField message;

    /**
     * constructor
     *
     * @param chatViewer chat viewer
     */
    public SendBar(ChatViewer chatViewer) {
        super(new GridBagLayout());
        this.chatViewer = chatViewer;
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.1;
        add(send = new JButton("send"), c);
        send.addActionListener(this);
        c.weightx = 0.9;
        add(message = new JTextField("message"), c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == send) {
            chatViewer.send(message.getText());
        }
    }
}
