package com.tuka.comiccharacters.ui;

import com.tuka.comiccharacters.ui.panel.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainApp {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Comic Book Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 5, 10, 10)); // 4 vertical panels

        PublisherPanel publisherPanel = new PublisherPanel();
        IssuePanel issuePanel = new IssuePanel();

        frame.add(publisherPanel);
        frame.add(new SeriesPanel(List.of(publisherPanel::refreshSeries, issuePanel::refreshSeries)));
        frame.add(issuePanel);
        frame.add(new CreatorPanel());
        frame.add(new CharacterPanel(issuePanel::refreshCharacters));


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