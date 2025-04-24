package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.service.SeriesService;
import com.tuka.comiccharacters.ui.MainApp;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeriesForm extends AbstractForm {

    private final JTextField titleField = new JTextField(20);
    private final JTextField startYearField = new JTextField(5);
    private final JTextField endYearField = new JTextField(5);
    private final JTextArea overviewArea = new JTextArea(4, 20);
    private final JComboBox<Publisher> publisherDropdown;
    private final SeriesService seriesService = new SeriesService();

    public SeriesForm() {
        this(null, null, null);
    }

    public SeriesForm(Series existingSeries, Runnable refreshCallback, JDialog parentDialog) {
        super(existingSeries == null ? "Add New Series" : "Edit Series");

        setLayout(new GridBagLayout()); // Use GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        overviewArea.setLineWrap(true);
        overviewArea.setWrapStyleWord(true);

        // Publisher Dropdown
        List<Publisher> publishers = new ArrayList<>();
        publishers.add(null); // for "None"
        PublisherService publisherService = new PublisherService();
        publishers.addAll(publisherService.getAllPublishers());

        publisherDropdown = new JComboBox<>(publishers.toArray(new Publisher[0]));
        publisherDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "None" : value.toString());
                return this;
            }
        });

        // Title Label and Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        add(titleField, gbc);

        // Start Year Label and Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Start Year:"), gbc);
        gbc.gridx = 1;
        add(startYearField, gbc);

        // End Year Label and Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("End Year:"), gbc);
        gbc.gridx = 1;
        add(endYearField, gbc);

        // Overview Label and Area
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Overview:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(overviewArea), gbc);

        // Publisher Label and Dropdown
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(new JLabel("Publisher:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        add(publisherDropdown, gbc);

        // Submit Button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

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

        // Save Button Logic
        removeAllSubmitListeners();
        addSubmitListener(_ -> {
            String title = titleField.getText().trim();
            String startYearText = startYearField.getText().trim();
            String endYearText = endYearField.getText().trim();
            String overview = overviewArea.getText().trim();
            Publisher selectedPublisher = (Publisher) publisherDropdown.getSelectedItem();

            if (title.isEmpty() || startYearText.isEmpty()) {
                MainApp.showError("Title and start year are required.");
                return;
            }

            try {
                int startYear = Integer.parseInt(startYearText);
                Integer endYear = endYearText.isEmpty() ? null : Integer.parseInt(endYearText);

                if (existingSeries == null) {
                    seriesService.addSeries(title, startYear, endYear, overview, selectedPublisher);
                    MainApp.showSuccess("Series added!");
                } else {
                    existingSeries.setTitle(title);
                    existingSeries.setStartYear(startYear);
                    existingSeries.setEndYear(endYear);
                    existingSeries.setOverview(overview);
                    existingSeries.setPublisher(selectedPublisher);
                    seriesService.updateSeries(existingSeries);
                    MainApp.showSuccess("Series updated!");
                }

                if (refreshCallback != null) refreshCallback.run();
                if (parentDialog != null) parentDialog.dispose();

            } catch (NumberFormatException ex) {
                MainApp.showError("Start and end year must be valid numbers.");
            }
        });
    }
}
