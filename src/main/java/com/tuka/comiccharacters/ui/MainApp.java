package com.tuka.comiccharacters.ui;

import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.ui.display.CreatorDisplay;
import com.tuka.comiccharacters.ui.display.SeriesDisplay;
import com.tuka.comiccharacters.ui.panel.CreatorPanel;
import com.tuka.comiccharacters.ui.panel.SeriesPanel;

import javax.swing.*;
import java.awt.*;

public class MainApp {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tuka's Comic Book Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // Two columns

        CreatorDisplay creatorDisplay = new CreatorDisplay();
        mainPanel.add(creatorDisplay);

        SeriesDisplay seriesDisplay = new SeriesDisplay();
        mainPanel.add(seriesDisplay);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addCreatorButton = new JButton("Add Creator");
        JButton addSeriesButton = new JButton("Add Series");

        // Add Creator logic
        addCreatorButton.addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Add Creator", true);
            dialog.setContentPane(new CreatorPanel());
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);

            creatorDisplay.refreshCreators();
        });

        // Add Series logic
        addSeriesButton.addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Add Series", true);
            dialog.setContentPane(new SeriesPanel());
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);

            seriesDisplay.refreshSeries();
        });

        buttonPanel.add(addCreatorButton);
        buttonPanel.add(addSeriesButton);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

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

    public static void showIssuePopup(Issue issue) {
        String message = "Series: " + issue.getSeries().getTitle() +
                "\nIssue Number: " + issue.getIssueNumber();
        JOptionPane.showMessageDialog(null, message, "Issue Details", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showSeriesPopup(Series series) {
        StringBuilder message = new StringBuilder();
        message.append("Publisher: ").append(series.getPublisher().getName()).append("\n")
                .append("Title: ").append(series.getTitle()).append("\n")
                .append("Start Year: ").append(series.getStartYear()).append("\n\n")
                .append("Issues:\n");

        for (Issue issue : series.getIssues()) {
            message.append("- Issue #").append(issue.getIssueNumber()).append("\n");
        }

        JOptionPane.showMessageDialog(null, message.toString(), "Series Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
