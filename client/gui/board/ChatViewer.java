package client.gui.board;

import libary.datastructure.linear.list.List;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ChatViewer extends JSplitPane implements PropertyChangeListener {

    private JPanel chatList;

    private JScrollPane left;
    private JScrollPane right;

    private List<Chat> chats;

    public ChatViewer() {
        super(JSplitPane.HORIZONTAL_SPLIT);
        setResizeWeight(0.2);
        chats = new List<>();
        this.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, this);
        chatList = new JPanel();
        chatList.setLayout(new BoxLayout(chatList, BoxLayout.Y_AXIS));
        add(left = new JScrollPane(chatList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), JSplitPane.LEFT);
        left.setViewportView(chatList);
        add(right = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), JSplitPane.RIGHT);
        addChat(new Chat(this, "NAME"));
        addChat(new Chat(this, "NAME"));
        addChat(new Chat(this, "NAME"));
        addChat(new Chat(this, "NAME"));
        addChat(new Chat(this, "NAME"));
        addChat(new Chat(this, "NAME"));
        addChat(new Chat(this, "NAME"));
    }

    public void addChat(Chat chat) {
        chatList.add(chat.getChatSelection());
        chats.append(chat);
        JButton button = chat.getChatSelection();
        button.setMaximumSize(new Dimension((int) (600 * 0.3), button.getFont().getSize() + 2));
    }

    public void setJTextArea(JTextArea area) {
        right.setViewportView(area);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == this) {
            for (Component c : chatList.getComponents()) {
                c.setMaximumSize(new Dimension((int) evt.getNewValue(), c.getHeight()));
            }
        }
    }
}
