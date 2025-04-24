package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.service.PublisherService;

import javax.swing.*;
import java.awt.*;

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class PublisherForm extends AbstractForm {

    private final JTextField nameField = new JTextField(20);
    private final PublisherService publisherService = new PublisherService();
    private Publisher editingPublisher;

    public PublisherForm() {
        super("Add New Publisher");
        setLayout(new GridBagLayout()); // Use GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Name Label and Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        add(nameField, gbc);

        // Submit Button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        addSubmitListener(_ -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showError("Publisher name is required.");
                return;
            }
            publisherService.addPublisher(name);
            showSuccess("Publisher added!");
            nameField.setText("");
        });
    }

    public PublisherForm(Publisher existingPublisher) {
        super("Edit Publisher");
        this.editingPublisher = existingPublisher;
        setLayout(new GridBagLayout()); // Use GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Name Label and Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        add(nameField, gbc);

        // Submit Button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        nameField.setText(existingPublisher.getName());

        addSubmitListener(_ -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showError("Publisher name is required.");
                return;
            }
            editingPublisher.setName(name);
            publisherService.updatePublisher(editingPublisher);
            showSuccess("Publisher updated!");
            SwingUtilities.getWindowAncestor(this).dispose();
        });
    }
}
