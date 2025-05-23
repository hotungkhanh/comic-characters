package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.service.PublisherService;

import javax.swing.*;

public class PublisherForm extends AbstractForm {

    private final JTextField nameField = new JTextField(20);
    private final PublisherService publisherService = new PublisherService();
    private Publisher editingPublisher;

    /**
     * Creates a new publisher form for adding publishers
     */
    public PublisherForm() {
        super("Add New Publisher");
        buildUI();
        setupSubmitAction();
    }

    /**
     * Creates a new publisher form for editing an existing publisher
     *
     * @param existingPublisher The publisher to edit
     */
    public PublisherForm(Publisher existingPublisher) {
        super("Edit Publisher");
        this.editingPublisher = existingPublisher;
        setEditMode(true);
        buildUI();
        populateFields(existingPublisher);
        setupEditAction();
    }

    @Override
    protected void buildUI() {
        int row = 0;

        // Set up name field with label
        row = addTextField("Name:", nameField, row, true);
    }

    /**
     * Sets up the submit action for adding new publishers
     */
    private void setupSubmitAction() {
        addSubmitListener(e -> {
            String name = nameField.getText().trim();
            addPublisher(name);
        });
    }

    /**
     * Sets up the submit action for editing publishers
     */
    private void setupEditAction() {
        removeAllSubmitListeners();
        addSubmitListener(e -> {
            String name = nameField.getText().trim();
            updatePublisher(name);
        });
    }

    /**
     * Populates form fields with data from an existing publisher
     *
     * @param publisher The publisher to load data from
     */
    private void populateFields(Publisher publisher) {
        nameField.setText(publisher.getName());
    }

    /**
     * Updates an existing publisher in the database
     *
     * @param name The updated name of the publisher
     */
    private void addPublisher(String name) {
        try {
            publisherService.save(new Publisher(name));
            showSuccess("Publisher added!");
            resetForm();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Updates an existing publisher in the database
     *
     * @param name The updated name of the publisher
     */
    private void updatePublisher(String name) {
        try {
            editingPublisher.setName(name);
            publisherService.save(editingPublisher);
            showSuccess("Publisher updated!");
            SwingUtilities.getWindowAncestor(this).dispose();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @Override
    protected void resetForm() {
        nameField.setText("");
    }
}
