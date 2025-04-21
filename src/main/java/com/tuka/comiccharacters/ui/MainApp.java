package com.tuka.comiccharacters.ui;

import com.tuka.comiccharacters.ui.browser.CreatorBrowser;
import com.tuka.comiccharacters.ui.browser.SeriesBrowser;
import com.tuka.comiccharacters.ui.form.CreatorForm;
import com.tuka.comiccharacters.ui.form.SeriesForm;

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
        CreatorBrowser creatorBrowser = new CreatorBrowser();
        SeriesBrowser seriesBrowser = new SeriesBrowser();

        // Main panel with 2 columns
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.add(creatorBrowser);
        mainPanel.add(seriesBrowser);

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
                creatorBrowser.filter(query);
                seriesBrowser.filter(query);
            }
        });

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addCreatorButton = new JButton("Add new Creator");
        JButton addSeriesButton = new JButton("Add new Series");

        addCreatorButton.addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Add new Creator", true);
            dialog.setContentPane(new CreatorForm());
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
            creatorBrowser.refreshEntities();
        });

        addSeriesButton.addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Add new Series", true);
            dialog.setContentPane(new SeriesForm());
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
            seriesBrowser.refreshEntities();
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
}
