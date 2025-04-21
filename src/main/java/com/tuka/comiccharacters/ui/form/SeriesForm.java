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
        super(existingSeries == null ? "Add Series" : "Edit Series", existingSeries == null ? "Add Series" : "Save Changes");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical arrangement

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

        addFormField("Title", titleField);
        add(Box.createVerticalStrut(5));
        addFormField("Start Year", startYearField);
        add(Box.createVerticalStrut(5));
        addFormField("End Year", endYearField);
        add(Box.createVerticalStrut(5));
        addFormField("Overview", new JScrollPane(overviewArea));
        add(Box.createVerticalStrut(5));
        addFormField("Publisher", publisherDropdown);
        add(Box.createVerticalStrut(10));

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