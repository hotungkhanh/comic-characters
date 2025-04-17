package com.tuka.comiccharacters.ui.panel;

import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.service.SeriesService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class SeriesPanel extends JPanel {
    public SeriesPanel() {
        this(null, null, null);
    }

    public SeriesPanel(Series existingSeries, Runnable refreshCallback, JDialog parentDialog) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Series"));

        JTextField titleField = new JTextField(15);
        JTextField yearField = new JTextField(5);

        PublisherService publisherService = new PublisherService();
        List<Publisher> allPublishers = publisherService.getAllPublishers();
        List<Publisher> publishersWithNull = new ArrayList<>();
        publishersWithNull.add(null);
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

        if (existingSeries != null) {
            titleField.setText(existingSeries.getTitle());
            yearField.setText(String.valueOf(existingSeries.getStartYear()));
            publisherDropdown.setSelectedItem(existingSeries.getPublisher());
        }

        add(new JLabel("Title:"));
        add(titleField);
        add(Box.createVerticalStrut(5));

        add(new JLabel("Start Year:"));
        add(yearField);
        add(Box.createVerticalStrut(5));

        add(new JLabel("Publisher:"));
        add(publisherDropdown);
        add(Box.createVerticalStrut(10));

        JButton saveButton = new JButton(existingSeries == null ? "Add Series" : "Save Changes");

        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String yearText = yearField.getText().trim();
            Publisher selectedPublisher = (Publisher) publisherDropdown.getSelectedItem();

            if (title.isEmpty() || yearText.isEmpty()) {
                showError("Please fill in title and start year.");
                return;
            }

            try {
                int year = Integer.parseInt(yearText);
                SeriesService service = new SeriesService();

                if (existingSeries == null) {
                    service.addSeries(title, year, selectedPublisher);
                    showSuccess("Series added!");
                } else {
                    existingSeries.setTitle(title);
                    existingSeries.setStartYear(year);
                    existingSeries.setPublisher(selectedPublisher);
                    service.updateSeries(existingSeries);
                    showSuccess("Series updated!");
                }

                if (refreshCallback != null) refreshCallback.run();
                if (parentDialog != null) parentDialog.dispose();

            } catch (NumberFormatException ex) {
                showError("Start Year must be a number.");
            }
        });

        add(saveButton);
    }
}
