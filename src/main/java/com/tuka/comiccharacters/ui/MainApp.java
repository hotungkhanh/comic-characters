package com.tuka.comiccharacters.ui;

import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.IssueService;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.service.SeriesService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Comic Book Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 4, 10, 10)); // 4 vertical panels side-by-side

        frame.add(createPublisherFormPanel());
        frame.add(createSeriesFormPanel());
        frame.add(createCharacterFormPanel());
        frame.add(createIssueFormPanel());

        frame.pack();
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
    }

    private static JPanel createPublisherFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Publisher"));

        JTextField nameField = new JTextField(15);
        JList<Series> seriesList = new JList<>(new SeriesService().getAllSeries().toArray(new Series[0]));
        seriesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        panel.add(new JLabel("Publisher Name:"));
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(5));
        panel.add(new JLabel("Select Series:"));
        JScrollPane scrollPane = new JScrollPane(seriesList);
        scrollPane.setPreferredSize(new Dimension(150, 80));
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(10));

        JButton addButton = new JButton("Add Publisher");
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            List<Series> selectedSeries = seriesList.getSelectedValuesList();

            if (name.isEmpty()) {
                showError("Please enter publisher name.");
                return;
            }

            PublisherService publisherService = new PublisherService();
            publisherService.addPublisher(name, selectedSeries);
            showSuccess("Publisher added!");
            nameField.setText("");
            seriesList.clearSelection();
        });

        panel.add(addButton);
        return panel;
    }

    private static JPanel createSeriesFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Series"));

        JTextField titleField = new JTextField(15);
        JTextField yearField = new JTextField(5);
        JComboBox<Publisher> publisherDropdown = new JComboBox<>(new PublisherService().getAllPublishers().toArray(new Publisher[0]));
        SeriesService service = new SeriesService();

        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(Box.createVerticalStrut(5));
        panel.add(new JLabel("Start Year:"));
        panel.add(yearField);
        panel.add(Box.createVerticalStrut(5));
        panel.add(new JLabel("Publisher:"));
        panel.add(publisherDropdown);
        panel.add(Box.createVerticalStrut(10));

        JButton addButton = new JButton("Add Series");
        addButton.addActionListener(e -> {
            String title = titleField.getText();
            String yearText = yearField.getText();
            Publisher selectedPublisher = (Publisher) publisherDropdown.getSelectedItem();

            if (title.isEmpty() || yearText.isEmpty() || selectedPublisher == null) {
                showError("Please complete all fields.");
                return;
            }
            try {
                int year = Integer.parseInt(yearText);
                service.addSeries(title, year, selectedPublisher);
                showSuccess("Comic Book added!");
                titleField.setText("");
                yearField.setText("");
            } catch (NumberFormatException ex) {
                showError("Start Year must be a number.");
            }
        });

        panel.add(addButton);
        return panel;
    }

    private static JPanel createCharacterFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Character"));

        JTextField nameField = new JTextField(10);
        JTextField aliasField = new JTextField(10);
        JTextField publisherField = new JTextField(10);
        CharacterService service = new CharacterService();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(5));
        panel.add(new JLabel("Alias:"));
        panel.add(aliasField);
        panel.add(Box.createVerticalStrut(5));
        panel.add(new JLabel("Publisher:"));
        panel.add(publisherField);
        panel.add(Box.createVerticalStrut(10));

        JButton addButton = new JButton("Add Character");
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            if (name.isEmpty()) {
                showError("Character name is required.");
                return;
            }
            service.addCharacter(name, aliasField.getText(), publisherField.getText());
            showSuccess("Character added!");
            nameField.setText("");
            aliasField.setText("");
            publisherField.setText("");
        });

        panel.add(addButton);
        return panel;
    }

    private static JPanel createIssueFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Issue"));

        SeriesService seriesService = new SeriesService();
        IssueService issueService = new IssueService();

        JComboBox<Series> seriesDropdown = new JComboBox<>(seriesService.getAllSeries().toArray(new Series[0]));
        JTextField issueNumberField = new JTextField(10);

        panel.add(new JLabel("Select Series:"));
        panel.add(seriesDropdown);
        panel.add(Box.createVerticalStrut(5));
        panel.add(new JLabel("Issue Number:"));
        panel.add(issueNumberField);
        panel.add(Box.createVerticalStrut(10));

        JButton addButton = new JButton("Add Issue");
        addButton.addActionListener(e -> {
            Series selectedSeries = (Series) seriesDropdown.getSelectedItem();
            String issueText = issueNumberField.getText();

            if (selectedSeries == null || issueText.isEmpty()) {
                showError("Please select a series and enter an issue number.");
                return;
            }

            try {
                int number = Integer.parseInt(issueText);
                issueService.addIssue(selectedSeries, number);
                showSuccess("Issue added!");
                issueNumberField.setText("");
            } catch (NumberFormatException ex) {
                showError("Issue number must be a number.");
            }
        });

        panel.add(addButton);
        return panel;
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    private static void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}