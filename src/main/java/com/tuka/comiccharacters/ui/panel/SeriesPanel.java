package com.tuka.comiccharacters.ui.panel;

import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.service.SeriesService;

import javax.swing.*;

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class SeriesPanel extends JPanel {
    public SeriesPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Series"));

        JTextField titleField = new JTextField(15);
        JTextField yearField = new JTextField(5);
        JComboBox<Publisher> publisherDropdown = new JComboBox<>(new PublisherService().getAllPublishers().toArray(new Publisher[0]));
        SeriesService service = new SeriesService();

        add(new JLabel("Title:"));
        add(titleField);
        add(Box.createVerticalStrut(5));
        add(new JLabel("Start Year:"));
        add(yearField);
        add(Box.createVerticalStrut(5));
        add(new JLabel("Publisher:"));
        add(publisherDropdown);
        add(Box.createVerticalStrut(10));

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

        add(addButton);
    }
}
