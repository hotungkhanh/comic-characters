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

        // UI Fields
        JTextField titleField = new JTextField(20);
        JTextField startYearField = new JTextField(5);
        JTextField endYearField = new JTextField(5);
        JTextArea overviewArea = new JTextArea(4, 20);
        overviewArea.setLineWrap(true);
        overviewArea.setWrapStyleWord(true);

        // Publisher Dropdown
        PublisherService publisherService = new PublisherService();
        List<Publisher> publishers = new ArrayList<>();
        publishers.add(null); // for "None"
        publishers.addAll(publisherService.getAllPublishers());

        JComboBox<Publisher> publisherDropdown = new JComboBox<>(publishers.toArray(new Publisher[0]));
        publisherDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                   boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "None" : value.toString());
                return this;
            }
        });

        // Pre-fill if editing
        if (existingSeries != null) {
            titleField.setText(existingSeries.getTitle());
            startYearField.setText(String.valueOf(existingSeries.getStartYear()));
            if (existingSeries.getEndYear() != null) {
                endYearField.setText(String.valueOf(existingSeries.getEndYear()));
            }
            overviewArea.setText(existingSeries.getOverview() != null ? existingSeries.getOverview() : "");
            publisherDropdown.setSelectedItem(existingSeries.getPublisher());
        }

        // Layout
        add(new JLabel("Title:"));
        add(titleField);
        add(Box.createVerticalStrut(5));

        add(new JLabel("Start Year:"));
        add(startYearField);
        add(Box.createVerticalStrut(5));

        add(new JLabel("End Year:"));
        add(endYearField);
        add(Box.createVerticalStrut(5));

        add(new JLabel("Overview:"));
        add(new JScrollPane(overviewArea));
        add(Box.createVerticalStrut(5));

        add(new JLabel("Publisher:"));
        add(publisherDropdown);
        add(Box.createVerticalStrut(10));

        // Save Button
        JButton saveButton = new JButton(existingSeries == null ? "Add Series" : "Save Changes");
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String startYearText = startYearField.getText().trim();
            String endYearText = endYearField.getText().trim();
            String overview = overviewArea.getText().trim();
            Publisher selectedPublisher = (Publisher) publisherDropdown.getSelectedItem();

            if (title.isEmpty() || startYearText.isEmpty()) {
                showError("Title and start year are required.");
                return;
            }

            try {
                int startYear = Integer.parseInt(startYearText);
                Integer endYear = endYearText.isEmpty() ? null : Integer.parseInt(endYearText);

                SeriesService service = new SeriesService();

                if (existingSeries == null) {
                    service.addSeries(title, startYear, endYear, overview, selectedPublisher);
                    showSuccess("Series added!");
                } else {
                    existingSeries.setTitle(title);
                    existingSeries.setStartYear(startYear);
                    existingSeries.setEndYear(endYear);
                    existingSeries.setOverview(overview);
                    existingSeries.setPublisher(selectedPublisher);
                    service.updateSeries(existingSeries);
                    showSuccess("Series updated!");
                }

                if (refreshCallback != null) refreshCallback.run();
                if (parentDialog != null) parentDialog.dispose();

            } catch (NumberFormatException ex) {
                showError("Start and end year must be valid numbers.");
            }
        });

        add(saveButton);
    }
}
