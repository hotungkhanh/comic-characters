package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.service.CreatorService;

import javax.swing.*;

public class CreatorForm extends AbstractForm {

    private final JTextField nameField = new JTextField(20);
    private final JTextArea overviewArea = new JTextArea(5, 20);
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

            addCreator(name, overview);
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

            updateCreator(name, overview);
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
    }

    /**
     * Adds a new creator to the database
     *
     * @param name     The name of the creator
     * @param overview The overview/description of the creator
     */
    private void addCreator(String name, String overview) {
        creatorService.addCreator(name, overview);
        showSuccess("Creator added!");
        resetForm();
    }

    /**
     * Updates an existing creator in the database
     *
     * @param name     The updated name of the creator
     * @param overview The updated overview/description of the creator
     */
    private void updateCreator(String name, String overview) {
        editingCreator.setName(name);
        editingCreator.setOverview(overview);
        creatorService.updateCreator(editingCreator);
        showSuccess("Creator updated successfully.");
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    @Override
    protected void resetForm() {
        nameField.setText("");
        overviewArea.setText("");
    }
}
