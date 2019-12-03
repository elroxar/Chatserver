package client.gui.board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Chat implements ActionListener {

    private final ChatViewer chatViewer;

    private final JButton chatSelection;
    private final JTextArea chat;

    public Chat(ChatViewer chatViewer, String chatName) {
        this.chatViewer = chatViewer;
        chatSelection = new JButton(chatName);
        JPanel list = new JPanel();
        chatSelection.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        chatSelection.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        chatSelection.addActionListener(this);
        chat = new JTextArea();
        chat.setEditable(false);
        chat.setLineWrap(true);
        chat.setWrapStyleWord(true);
    }

    public JButton getChatSelection() {
        return chatSelection;
    }

    public JTextArea getChat() {
        return chat;
    }

    public void addMessage(String name, String time, String message) {
        chat.append("<" + time + "> name:" + message + "\n\n");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chatSelection)
            chatViewer.setJTextArea(chat);
    }
}
