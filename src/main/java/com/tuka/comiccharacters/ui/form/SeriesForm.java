package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.service.SeriesService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SeriesForm extends AbstractForm {

    private final JTextField titleField = new JTextField(20);
    private final JTextField startYearField = new JTextField(5);
    private final JTextField endYearField = new JTextField(5);
    private final JTextArea overviewArea = new JTextArea(4, 20);
    private final JComboBox<Publisher> publisherDropdown;
    private final SeriesService seriesService = new SeriesService();

    private Series existingSeries;
    private Runnable refreshCallback;
    private JDialog parentDialog;

    /**
     * Creates a form for adding a new series
     */
    public SeriesForm() {
        super("Add New Series");

        // Initialize publisher dropdown
        publisherDropdown = createPublisherDropdown();

        buildUI();
        setupSubmitAction();
    }

    /**
     * Creates a form for editing an existing series
     *
     * @param existingSeries  The series to edit
     * @param refreshCallback Callback to run after form submission
     * @param parentDialog    The parent dialog to close after submission
     */
    public SeriesForm(Series existingSeries, Runnable refreshCallback, JDialog parentDialog) {
        super(existingSeries == null ? "Add New Series" : "Edit Series");

        this.existingSeries = existingSeries;
        this.refreshCallback = refreshCallback;
        this.parentDialog = parentDialog;

        // Initialize publisher dropdown
        publisherDropdown = createPublisherDropdown();

        if (existingSeries != null) {
            setEditMode(true);
        }

        buildUI();

        if (existingSeries != null) {
            populateFields(existingSeries);
        }

        setupSubmitAction();
    }

    @Override
    protected void buildUI() {
        int row = 0;

        // Add fields to the form
        row = addTextField("Title:", titleField, row, true);
        row = addTextField("Start Year:", startYearField, row, true);
        row = addTextField("End Year:", endYearField, row, false);
        row = addTextArea("Overview:", overviewArea, row, 4, false);
        row = addDropdown("Publisher:", publisherDropdown, row, false);
    }

    /**
     * Creates the publisher dropdown with a "None" option
     *
     * @return The publisher dropdown
     */
    private JComboBox<Publisher> createPublisherDropdown() {
        List<Publisher> publishers = new ArrayList<>();
        publishers.add(null); // for "None"
        PublisherService publisherService = new PublisherService();
        publishers.addAll(publisherService.getAllEntities());

        return createNullableDropdown(publishers.toArray(new Publisher[0]), "None");
    }

    /**
     * Sets up the submit action for the form
     */
    private void setupSubmitAction() {
        removeAllSubmitListeners();
        addSubmitListener(_ -> saveOrUpdateSeries());
    }

    /**
     * Populates form fields with data from an existing series
     *
     * @param series The series to load data from
     */
    private void populateFields(Series series) {
        titleField.setText(series.getTitle());
        startYearField.setText(String.valueOf(series.getStartYear()));

        if (series.getEndYear() != null) {
            endYearField.setText(String.valueOf(series.getEndYear()));
        }

        overviewArea.setText(series.getOverview() != null ? series.getOverview() : "");
        publisherDropdown.setSelectedItem(series.getPublisher());
    }

    /**
     * Validates and saves or updates a series
     */
    private void saveOrUpdateSeries() {
        if (!validateForm()) {
            return;
        }

        try {
            String title = titleField.getText().trim();
            int startYear = Integer.parseInt(startYearField.getText().trim());

            String endYearText = endYearField.getText().trim();
            Integer endYear = endYearText.isEmpty() ? null : Integer.parseInt(endYearText);

            String overview = overviewArea.getText().trim();
            Publisher selectedPublisher = (Publisher) publisherDropdown.getSelectedItem();

            if (isEditMode && existingSeries != null) {
                updateSeries(title, startYear, endYear, overview, selectedPublisher);
            } else {
                addSeries(title, startYear, endYear, overview, selectedPublisher);
            }

            // Execute callback and close dialog if needed
            if (refreshCallback != null) refreshCallback.run();
            if (parentDialog != null) parentDialog.dispose();

        } catch (NumberFormatException ex) {
            showError("Start and end year must be valid numbers.");
            startYearField.requestFocus();
        }
    }

    /**
     * Adds a new series to the database
     *
     * @param title     The title of the series
     * @param startYear The start year of the series
     * @param endYear   The end year of the series (may be null)
     * @param overview  The overview of the series
     * @param publisher The publisher of the series (may be null)
     */
    private void addSeries(String title, int startYear, Integer endYear,
                           String overview, Publisher publisher) {
        try {
            seriesService.addSeries(title, startYear, endYear, overview, publisher);
            showSuccess("Series added!");
            resetForm();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Updates an existing series in the database
     *
     * @param title     The updated title of the series
     * @param startYear The updated start year of the series
     * @param endYear   The updated end year of the series (may be null)
     * @param overview  The updated overview of the series
     * @param publisher The updated publisher of the series (may be null)
     */
    private void updateSeries(String title, int startYear, Integer endYear,
                              String overview, Publisher publisher) {
        try {
            existingSeries.setTitle(title);
            existingSeries.setStartYear(startYear);
            existingSeries.setEndYear(endYear);
            existingSeries.setOverview(overview);
            existingSeries.setPublisher(publisher);

            seriesService.updateSeries(existingSeries);
            showSuccess("Series updated!");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Validates the form fields
     *
     * @return Whether the form is valid
     */
    private boolean validateForm() {
        if (titleField.getText().trim().isEmpty()) {
            showError("Title is required.");
            titleField.requestFocus();
            return false;
        }

        if (startYearField.getText().trim().isEmpty()) {
            showError("Start year is required.");
            startYearField.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    protected void resetForm() {
        titleField.setText("");
        startYearField.setText("");
        endYearField.setText("");
        overviewArea.setText("");
        publisherDropdown.setSelectedItem(null);
    }
}
