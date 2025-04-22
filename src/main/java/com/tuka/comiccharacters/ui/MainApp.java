package com.tuka.comiccharacters.ui;

import com.tuka.comiccharacters.ui.browser.CharacterBrowser;
import com.tuka.comiccharacters.ui.browser.CreatorBrowser;
import com.tuka.comiccharacters.ui.browser.PublisherBrowser;
import com.tuka.comiccharacters.ui.browser.SeriesBrowser;
import com.tuka.comiccharacters.ui.form.CreatorForm;
import com.tuka.comiccharacters.ui.form.PublisherForm;
import com.tuka.comiccharacters.ui.form.SeriesForm;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class MainApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tuka's Comic Book Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // Search bar
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(800, 30)); // Increased width for 4 columns

        // Browsers
        PublisherBrowser publisherBrowser = new PublisherBrowser();
        CreatorBrowser creatorBrowser = new CreatorBrowser();
        SeriesBrowser seriesBrowser = new SeriesBrowser();
        CharacterBrowser characterBrowser = new CharacterBrowser(); // Create CharacterBrowser

        // Main panel with 4 columns (for browsers)
        JPanel browserPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        browserPanel.add(publisherBrowser);
        browserPanel.add(creatorBrowser);
        browserPanel.add(seriesBrowser);
        browserPanel.add(characterBrowser); // Add CharacterBrowser

        // Button panels for each column
        JPanel publisherButtonPanel = getPublisherButtonPanel(frame, publisherBrowser);
        JPanel creatorButtonPanel = getCreatorButtonPanel(frame, creatorBrowser);
        JPanel seriesButtonPanel = getSeriesButtonPanel(frame, seriesBrowser);
        JPanel characterButtonPanel = getCharacterButtonPanel(frame, characterBrowser); // Get Character button panel

        // Main content panel to hold browsers and their buttons in separate rows
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(browserPanel, BorderLayout.CENTER);

        JPanel buttonRowPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonRowPanel.add(publisherButtonPanel);
        buttonRowPanel.add(creatorButtonPanel);
        buttonRowPanel.add(seriesButtonPanel);
        buttonRowPanel.add(characterButtonPanel); // Add Character button panel
        contentPanel.add(buttonRowPanel, BorderLayout.SOUTH);

        // Search logic (filter all browsers)
        DocumentListener filterListener = new javax.swing.event.DocumentListener() {
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
                publisherBrowser.filter(query);
                creatorBrowser.filter(query);
                seriesBrowser.filter(query);
                characterBrowser.filter(query); // Filter CharacterBrowser
            }
        };
        searchField.getDocument().addDocumentListener(filterListener);

        // Wrap search bar and content panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchField, BorderLayout.NORTH);
        topPanel.add(contentPanel, BorderLayout.CENTER);

        frame.add(topPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel getCharacterButtonPanel(JFrame frame, CharacterBrowser characterBrowser) {
        JPanel characterButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addCharacterButton = new JButton("Add New Characters");
        addCharacterButton.addActionListener(_ -> {
            JDialog dialog = new JDialog(frame, "Add New Character", true);
//            dialog.setContentPane(new CharacterForm());
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
            characterBrowser.refreshEntities();
        });
        characterButtonPanel.add(addCharacterButton);
        return characterButtonPanel;
    }

    private static JPanel getSeriesButtonPanel(JFrame frame, SeriesBrowser seriesBrowser) {
        JPanel seriesButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addSeriesButton = new JButton("Add New Series");
        addSeriesButton.addActionListener(_ -> {
            JDialog dialog = new JDialog(frame, "Add New Series", true);
            dialog.setContentPane(new SeriesForm());
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
            seriesBrowser.refreshEntities();
        });
        seriesButtonPanel.add(addSeriesButton);
        return seriesButtonPanel;
    }

    private static JPanel getCreatorButtonPanel(JFrame frame, CreatorBrowser creatorBrowser) {
        JPanel creatorButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addCreatorButton = new JButton("Add New Creators");
        addCreatorButton.addActionListener(_ -> {
            JDialog dialog = new JDialog(frame, "Add New Creator", true);
            dialog.setContentPane(new CreatorForm());
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
            creatorBrowser.refreshEntities();
        });
        creatorButtonPanel.add(addCreatorButton);
        return creatorButtonPanel;
    }

    private static JPanel getPublisherButtonPanel(JFrame frame, PublisherBrowser publisherBrowser) {
        JPanel publisherButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addPublisherButton = new JButton("Add New Publishers");
        addPublisherButton.addActionListener(_ -> {
            JDialog dialog = new JDialog(frame, "Add New Publisher", true);
            dialog.setContentPane(new PublisherForm());
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
            publisherBrowser.refreshEntities();
        });
        publisherButtonPanel.add(addPublisherButton);
        return publisherButtonPanel;
    }

    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}