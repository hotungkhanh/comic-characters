package com.tuka.comiccharacters.ui;

import com.tuka.comiccharacters.ui.panel.CharacterPanel;
import com.tuka.comiccharacters.ui.panel.IssuePanel;
import com.tuka.comiccharacters.ui.panel.PublisherPanel;
import com.tuka.comiccharacters.ui.panel.SeriesPanel;

import javax.swing.*;
import java.awt.*;

public class MainApp {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Comic Book Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 4, 10, 10)); // 4 vertical panels

        frame.add(new PublisherPanel());
        frame.add(new SeriesPanel());
        frame.add(new CharacterPanel());
        frame.add(new IssuePanel());

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}