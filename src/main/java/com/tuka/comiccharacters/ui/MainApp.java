package com.tuka.comiccharacters.ui;

import com.tuka.comiccharacters.ui.panel.*;

import javax.swing.*;
import java.awt.*;

public class MainApp {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Comic Book Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 5, 10, 10)); // 4 vertical panels

        frame.add(new PublisherPanel());
        frame.add(new SeriesPanel());
        frame.add(new IssuePanel());
        frame.add(new CreatorPanel());
        frame.add(new CharacterPanel());


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