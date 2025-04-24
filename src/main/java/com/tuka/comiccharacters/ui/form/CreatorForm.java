package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.ui.MainApp;

import javax.swing.*;
import java.awt.*;

public class CreatorForm extends AbstractForm {

    private final JTextField nameField = new JTextField(20);
    private final JTextArea overviewArea = new JTextArea(5, 20);
    private final CreatorService creatorService = new CreatorService();

    public CreatorForm() {
        super("Add New Creator");
        setLayout(new GridBagLayout()); // Use GridBagLayout for more control
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5); // Add some padding
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make the text field fill the available horizontal space
        gbc.weightx = 1.0; // Give the text field some weight

        // Name Label and Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Name:"), gbc); // Add the label
        gbc.gridx = 1;
        add(nameField, gbc); // Add the text field

        // Overview Label and Area
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Span only one column for the label
        gbc.anchor = GridBagConstraints.WEST; // Keep label to the left
        add(new JLabel("Overview:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 1; // The text area will take up one column.
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;  // Make the text area fill both directions
        add(new JScrollPane(overviewArea), gbc);

        // Submit Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // span 2 columns
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE; // Don't fill.
        gbc.anchor = GridBagConstraints.CENTER; // Center the button.
        add(submitButton, gbc);

        overviewArea.setLineWrap(true);
        overviewArea.setWrapStyleWord(true);

        addSubmitListener(_ -> {
            String name = nameField.getText().trim();
            String overview = overviewArea.getText().trim();

            if (name.isEmpty()) {
                MainApp.showError("Name is required.");
                return;
            }

            creatorService.addCreator(name, overview);
            MainApp.showSuccess("Creator added!");
            nameField.setText("");
            overviewArea.setText("");
        });
    }

    public CreatorForm(Creator existingCreator) {
        this(); // Call the default constructor to set up the layout

        setSubmitButtonText("Save");
        nameField.setText(existingCreator.getName());
        overviewArea.setText(existingCreator.getOverview());

        removeAllSubmitListeners();

        addSubmitListener(_ -> {
            String name = nameField.getText().trim();
            String overview = overviewArea.getText().trim();

            if (name.isEmpty()) {
                MainApp.showError("Name is required.");
                return;
            }

            existingCreator.setName(name);
            existingCreator.setOverview(overview);
            creatorService.updateCreator(existingCreator);
            MainApp.showSuccess("Creator updated successfully.");
            SwingUtilities.getWindowAncestor(this).dispose();
        });
    }
}
