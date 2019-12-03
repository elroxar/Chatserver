package client.gui;

import client.gui.board.Board;
import client.gui.board.ChatViewer;
import client.gui.board.LowerBar;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {

    public GUI() {
        super("Chat");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        add(new ChatViewer(), BorderLayout.CENTER);
        add(new LowerBar(), BorderLayout.SOUTH);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new GUI();
    }

    private void initBoard() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        add(new Board());
    }

}
