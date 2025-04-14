package com.tuka.comiccharacters.ui.panel;

import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.service.SeriesService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class SeriesPanel extends JPanel {
    public SeriesPanel(Runnable onSeriesAdded) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Series"));

        JTextField titleField = new JTextField(15);
        JTextField yearField = new JTextField(5);

        PublisherService publisherService = new PublisherService();
        List<Publisher> allPublishers = publisherService.getAllPublishers();
        List<Publisher> publishersWithNull = new ArrayList<>();
        publishersWithNull.add(null); // represents 'None'
        publishersWithNull.addAll(allPublishers);

        JComboBox<Publisher> publisherDropdown = new JComboBox<>(publishersWithNull.toArray(new Publisher[0]));
        publisherDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                   boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "None" : value.toString());
                return this;
            }
        });

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
            String title = titleField.getText().trim();
            String yearText = yearField.getText().trim();
            Publisher selectedPublisher = (Publisher) publisherDropdown.getSelectedItem(); // can be null

            if (title.isEmpty() || yearText.isEmpty()) {
                showError("Please fill in title and start year.");
                return;
            }

            try {
                int year = Integer.parseInt(yearText);
                service.addSeries(title, year, selectedPublisher); // pass null if no publisher
                showSuccess("Series added!");
                titleField.setText("");
                yearField.setText("");
                publisherDropdown.setSelectedIndex(0);
            } catch (NumberFormatException ex) {
                showError("Start Year must be a number.");
            }

            // Refresh Publisher panel
            onSeriesAdded.run();
        });

        add(addButton);
    }
}
