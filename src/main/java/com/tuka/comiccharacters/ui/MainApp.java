package com.tuka.comiccharacters.ui;

import com.tuka.comiccharacters.ui.browser.CharacterBrowser;
import com.tuka.comiccharacters.ui.browser.CreatorBrowser;
import com.tuka.comiccharacters.ui.browser.PublisherBrowser;
import com.tuka.comiccharacters.ui.browser.SeriesBrowser;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Enumeration;

public class MainApp {
    public static void main(String[] args) {
        setGlobalFont(new Font("Comic Sans MS", Font.BOLD, 20));

        JFrame frame = new JFrame("Tuka's Comic Book Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // Search bar with placeholder text
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(800, 50));
        addPlaceholderText(searchField, "Search Publishers, Creators, Series, and Characters");

        // Browsers
        PublisherBrowser publisherBrowser = new PublisherBrowser(frame);
        CreatorBrowser creatorBrowser = new CreatorBrowser(frame);
        SeriesBrowser seriesBrowser = new SeriesBrowser(frame);
        CharacterBrowser characterBrowser = new CharacterBrowser(frame);

        // Main panel with 4 columns
        JPanel browserPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        browserPanel.add(publisherBrowser);
        browserPanel.add(creatorBrowser);
        browserPanel.add(seriesBrowser);
        browserPanel.add(characterBrowser);

        // Filter logic
        DocumentListener filterListener = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }

            private void filter() {
                String query = searchField.getText().trim();
                if (query.equals("Search Publishers, Creators, Series, and Characters")) {
                    query = "";
                }
                publisherBrowser.filter(query);
                creatorBrowser.filter(query);
                seriesBrowser.filter(query);
                characterBrowser.filter(query);
            }
        };
        searchField.getDocument().addDocumentListener(filterListener);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchField, BorderLayout.NORTH);
        topPanel.add(browserPanel, BorderLayout.CENTER);

        frame.add(topPanel, BorderLayout.CENTER);

        frame.setSize(1400, 600); // preferred small size
        frame.setLocationRelativeTo(null); // center on screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // start with fullscreen
        frame.setVisible(true);
    }

    static void addPlaceholderText(JTextField textField, String placeholderText) {
        textField.setForeground(Color.GRAY);
        textField.setText(placeholderText);

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholderText)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholderText);
                }
            }
        });
    }

    private static void setGlobalFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Font cannot be null");
        }

        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, font);
            }
        }
    }

    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
