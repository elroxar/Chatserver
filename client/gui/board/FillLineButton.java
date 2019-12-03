package client.gui.board;

import javax.swing.*;
import java.awt.*;

public class FillLineButton extends JButton {

    public FillLineButton(String chatName) {
        super(chatName);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        System.out.println(getParent().getParent().getWidth());
        return new Dimension(getParent().getParent().getWidth(), getFont().getSize() + 20);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

}
