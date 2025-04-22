package com.tuka.comiccharacters.ui;

import com.tuka.comiccharacters.ui.browser.CharacterBrowser;
import com.tuka.comiccharacters.ui.browser.CreatorBrowser;
import com.tuka.comiccharacters.ui.browser.PublisherBrowser;
import com.tuka.comiccharacters.ui.browser.SeriesBrowser;
import com.tuka.comiccharacters.ui.form.CharacterForm;
import com.tuka.comiccharacters.ui.form.CreatorForm;
import com.tuka.comiccharacters.ui.form.PublisherForm;
import com.tuka.comiccharacters.ui.form.SeriesForm;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Enumeration;

public class MainApp {
    public static void main(String[] args) {
        setGlobalFont(new Font("Comic Sans MS", Font.BOLD, 20));

        JFrame frame = new JFrame("Tuka's Comic Book Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // Search bar
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(800, 50));

        // Browsers
        PublisherBrowser publisherBrowser = new PublisherBrowser();
        CreatorBrowser creatorBrowser = new CreatorBrowser();
        SeriesBrowser seriesBrowser = new SeriesBrowser();
        CharacterBrowser characterBrowser = new CharacterBrowser();

        // Main panel with 4 columns
        JPanel browserPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        browserPanel.add(publisherBrowser);
        browserPanel.add(creatorBrowser);
        browserPanel.add(seriesBrowser);
        browserPanel.add(characterBrowser);

        // Button panels
        JPanel publisherButtonPanel = getPublisherButtonPanel(frame, publisherBrowser);
        JPanel creatorButtonPanel = getCreatorButtonPanel(frame, creatorBrowser);
        JPanel seriesButtonPanel = getSeriesButtonPanel(frame, seriesBrowser);
        JPanel characterButtonPanel = getCharacterButtonPanel(frame, characterBrowser);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(browserPanel, BorderLayout.CENTER);

        JPanel buttonRowPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonRowPanel.add(publisherButtonPanel);
        buttonRowPanel.add(creatorButtonPanel);
        buttonRowPanel.add(seriesButtonPanel);
        buttonRowPanel.add(characterButtonPanel);
        contentPanel.add(buttonRowPanel, BorderLayout.SOUTH);

        // Filter logic
        DocumentListener filterListener = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }

            private void filter() {
                String query = searchField.getText().trim();
                publisherBrowser.filter(query);
                creatorBrowser.filter(query);
                seriesBrowser.filter(query);
                characterBrowser.filter(query);
            }
        };
        searchField.getDocument().addDocumentListener(filterListener);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchField, BorderLayout.NORTH);
        topPanel.add(contentPanel, BorderLayout.CENTER);

        frame.add(topPanel, BorderLayout.CENTER);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        frame.setVisible(true);
    }

    private static void setGlobalFont(Font font) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, font);
            }
        }
    }

    private static JPanel getCharacterButtonPanel(JFrame frame, CharacterBrowser characterBrowser) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton button = new JButton("Add New Characters");
        button.addActionListener(_ -> {
            JDialog dialog = new JDialog(frame, "Add New Character", true);
            dialog.setContentPane(new CharacterForm());
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
            characterBrowser.refreshEntities();
        });
        panel.add(button);
        return panel;
    }

    private static JPanel getSeriesButtonPanel(JFrame frame, SeriesBrowser seriesBrowser) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton button = new JButton("Add New Series");
        button.addActionListener(_ -> {
            JDialog dialog = new JDialog(frame, "Add New Series", true);
            dialog.setContentPane(new SeriesForm());
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
            seriesBrowser.refreshEntities();
        });
        panel.add(button);
        return panel;
    }

    private static JPanel getCreatorButtonPanel(JFrame frame, CreatorBrowser creatorBrowser) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton button = new JButton("Add New Creators");
        button.addActionListener(_ -> {
            JDialog dialog = new JDialog(frame, "Add New Creator", true);
            dialog.setContentPane(new CreatorForm());
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
            creatorBrowser.refreshEntities();
        });
        panel.add(button);
        return panel;
    }

    private static JPanel getPublisherButtonPanel(JFrame frame, PublisherBrowser publisherBrowser) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton button = new JButton("Add New Publishers");
        button.addActionListener(_ -> {
            JDialog dialog = new JDialog(frame, "Add New Publisher", true);
            dialog.setContentPane(new PublisherForm());
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
            publisherBrowser.refreshEntities();
        });
        panel.add(button);
        return panel;
    }

    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
