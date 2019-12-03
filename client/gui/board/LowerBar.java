package client.gui.board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LowerBar extends JPanel implements ActionListener {

    private JButton send;
    private JTextField message;

    public LowerBar() {
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(send = new JButton("send"), c);
        send.addActionListener(this);
        c.weightx = 0.9;
        add(message = new JTextField("enter message"), c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == send)
            System.out.println("send message: " + message.getText());
    }
}
