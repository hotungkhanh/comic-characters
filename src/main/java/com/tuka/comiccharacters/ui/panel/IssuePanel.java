package com.tuka.comiccharacters.ui.panel;

import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.IssueService;
import com.tuka.comiccharacters.service.SeriesService;

import javax.swing.*;

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class IssuePanel extends JPanel {
    public IssuePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Issue"));

        SeriesService seriesService = new SeriesService();
        IssueService issueService = new IssueService();

        JComboBox<Series> seriesDropdown = new JComboBox<>(seriesService.getAllSeries().toArray(new Series[0]));
        JTextField issueNumberField = new JTextField(10);

        add(new JLabel("Select Series:"));
        add(seriesDropdown);
        add(Box.createVerticalStrut(5));
        add(new JLabel("Issue Number:"));
        add(issueNumberField);
        add(Box.createVerticalStrut(10));

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

        add(addButton);
    }
}
