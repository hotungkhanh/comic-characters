package com.tuka.comiccharacters.ui;

import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.SeriesService;
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

        // Search bar
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(400, 30));

        // Displays
        CreatorDisplay creatorDisplay = new CreatorDisplay();
        SeriesDisplay seriesDisplay = new SeriesDisplay();

        // Main panel with 2 columns
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.add(creatorDisplay);
        mainPanel.add(seriesDisplay);

        // Search logic (filter both displays)
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            private void filter() {
                String query = searchField.getText().trim();
                creatorDisplay.filter(query);
                seriesDisplay.filter(query);
            }
        });

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addCreatorButton = new JButton("Add Creator");
        JButton addSeriesButton = new JButton("Add Series");

        addCreatorButton.addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Add Creator", true);
            dialog.setContentPane(new CreatorPanel());
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
            creatorDisplay.refreshCreators();
        });

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

        // Wrap everything in the frame
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchField, BorderLayout.NORTH);
        topPanel.add(mainPanel, BorderLayout.CENTER);

        frame.add(topPanel, BorderLayout.CENTER);
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

    public static void showSeriesPopup(Series series, Runnable refreshCallback) {
        JDialog dialog = new JDialog((Frame) null, "Series Details", true);
        dialog.setLayout(new BorderLayout());

        String publisherText = series.getPublisher() != null ? series.getPublisher().toString() : "None";
        String message = series +
                "\nPublisher: " + publisherText +
                "\nIssues: " + series.getIssues().size();

        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        editBtn.addActionListener(e -> {
            dialog.dispose();
            showEditSeriesDialog(series, refreshCallback);
        });

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog, "Delete this series?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new SeriesService().deleteSeries(series.getId());
                showSuccess("Series deleted.");
                dialog.dispose();
                refreshCallback.run();
            }
        });

        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(350, 220);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }


    public static void showEditSeriesDialog(Series series, Runnable refreshCallback) {
        JDialog dialog = new JDialog((Frame) null, "Edit Series", true);
        SeriesPanel panel = new SeriesPanel(series, refreshCallback, dialog);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
