package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.service.PublisherService;

import javax.swing.*;

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class PublisherForm extends AbstractForm {

    private final JTextField nameField = new JTextField(20);
    private final PublisherService publisherService = new PublisherService();
    private Publisher editingPublisher;

    public PublisherForm() {
        super("Add New Publisher");
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        addFormField("Name", nameField);

        addSubmitListener(e -> {
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
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        addFormField("Name", nameField);
        nameField.setText(existingPublisher.getName());

        addSubmitListener(e -> {
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