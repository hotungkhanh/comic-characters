package com.tuka.comiccharacters.ui;

import com.tuka.comiccharacters.ui.browser.CreatorBrowser;
import com.tuka.comiccharacters.ui.browser.PublisherBrowser;
import com.tuka.comiccharacters.ui.browser.SeriesBrowser;
import com.tuka.comiccharacters.ui.form.CreatorForm;
import com.tuka.comiccharacters.ui.form.PublisherForm;
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
        searchField.setPreferredSize(new Dimension(600, 30));

        // Browsers
        PublisherBrowser publisherBrowser = new PublisherBrowser();
        CreatorBrowser creatorBrowser = new CreatorBrowser();
        SeriesBrowser seriesBrowser = new SeriesBrowser();

        // Main panel with 3 columns (for browsers)
        JPanel browserPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        browserPanel.add(publisherBrowser);
        browserPanel.add(creatorBrowser);
        browserPanel.add(seriesBrowser);

        // Button panels for each column
        JPanel publisherButtonPanel = getPublisherButtonPanel(frame, publisherBrowser);
        JPanel creatorButtonPanel = getCreatorButtonPanel(frame, creatorBrowser);
        JPanel seriesButtonPanel = getSeriesButtonPanel(frame, seriesBrowser);

        // Main content panel to hold browsers and their buttons in separate rows
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(browserPanel, BorderLayout.CENTER);

        JPanel buttonRowPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonRowPanel.add(publisherButtonPanel);
        buttonRowPanel.add(creatorButtonPanel);
        buttonRowPanel.add(seriesButtonPanel);
        contentPanel.add(buttonRowPanel, BorderLayout.SOUTH);

        // Wrap search bar and content panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchField, BorderLayout.NORTH);
        topPanel.add(contentPanel, BorderLayout.CENTER);

        frame.add(topPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel getSeriesButtonPanel(JFrame frame, SeriesBrowser seriesBrowser) {
        JPanel seriesButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addSeriesButton = new JButton("Add new Series");
        addSeriesButton.addActionListener(_ -> {
            JDialog dialog = new JDialog(frame, "Add new Series", true);
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
        JButton addCreatorButton = new JButton("Add new Creator");
        addCreatorButton.addActionListener(_ -> {
            JDialog dialog = new JDialog(frame, "Add new Creator", true);
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
        JButton addPublisherButton = new JButton("Add new Publisher");
        addPublisherButton.addActionListener(_ -> {
            JDialog dialog = new JDialog(frame, "Add new Publisher", true);
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