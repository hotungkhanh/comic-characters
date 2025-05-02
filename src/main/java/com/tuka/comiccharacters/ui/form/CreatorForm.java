package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.service.CreatorService;

import javax.swing.*;

public class CreatorForm extends AbstractForm {

    private final JTextField nameField = new JTextField(20);
    private final JTextArea overviewArea = new JTextArea(5, 20);
    private final JTextField imageUrlField = new JTextField(20);
    private final CreatorService creatorService = new CreatorService();
    private Creator editingCreator;

    /**
     * Creates a new creator form for adding creators
     */
    public CreatorForm() {
        super("Add New Creator");
        buildUI();
        setupSubmitAction();
    }

    /**
     * Creates a new creator form for editing an existing creator
     *
     * @param existingCreator The creator to edit
     */
    public CreatorForm(Creator existingCreator) {
        super("Edit Creator");
        this.editingCreator = existingCreator;
        setEditMode(true);
        buildUI();
        populateFields(existingCreator);
        setupEditAction();
    }

    @Override
    protected void buildUI() {
        int row = 0;

        // Set up text fields with proper labels
        row = addTextField("Name:", nameField, row, true);

        // Add image URL field
        row = addTextField("Image URL:", imageUrlField, row, false);

        // Set up text area with proper configuration
        overviewArea.setLineWrap(true);
        overviewArea.setWrapStyleWord(true);
        row = addTextArea("Overview:", overviewArea, row, 5, false);
    }

    /**
     * Sets up the submit action for adding new creators
     */
    private void setupSubmitAction() {
        addSubmitListener(e -> {
            if (!validateForm()) {
                return;
            }

            String name = nameField.getText().trim();
            String overview = overviewArea.getText().trim();
            String imageUrl = imageUrlField.getText().trim();

            addCreator(name, overview, imageUrl);
        });
    }

    /**
     * Sets up the submit action for editing creators
     */
    private void setupEditAction() {
        removeAllSubmitListeners();
        addSubmitListener(e -> {
            if (!validateForm()) {
                return;
            }

            String name = nameField.getText().trim();
            String overview = overviewArea.getText().trim();
            String imageUrl = imageUrlField.getText().trim();

            updateCreator(name, overview, imageUrl);
        });
    }

    /**
     * Validates the form fields
     *
     * @return Whether the form is valid
     */
    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Creator name is required.");
            nameField.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Populates form fields with data from an existing creator
     *
     * @param creator The creator to load data from
     */
    private void populateFields(Creator creator) {
        nameField.setText(creator.getName());
        overviewArea.setText(creator.getOverview() != null ? creator.getOverview() : "");
        imageUrlField.setText(creator.getImageUrl() != null ? creator.getImageUrl() : "");
    }

    /**
     * Adds a new creator to the database
     *
     * @param name     The name of the creator
     * @param overview The overview/description of the creator
     * @param imageUrl The URL to the creator's image
     */
    private void addCreator(String name, String overview, String imageUrl) {
        try {
            creatorService.save(new Creator(name, overview, imageUrl));
            showSuccess("Creator added!");
            resetForm();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Updates an existing creator in the database
     *
     * @param name     The updated name of the creator
     * @param overview The updated overview/description of the creator
     * @param imageUrl The URL to the creator's image
     */
    private void updateCreator(String name, String overview, String imageUrl) {
        try {
            editingCreator.setName(name);
            editingCreator.setOverview(overview);
            editingCreator.setImageUrl(imageUrl.isEmpty() ? null : imageUrl);
            creatorService.save(editingCreator);
            showSuccess("Creator updated successfully.");
            SwingUtilities.getWindowAncestor(this).dispose();
        } catch (IllegalArgumentException e) {
        showError(e.getMessage());
    }
    }

    @Override
    protected void resetForm() {
        nameField.setText("");
        overviewArea.setText("");
        imageUrlField.setText("");
    }
}
