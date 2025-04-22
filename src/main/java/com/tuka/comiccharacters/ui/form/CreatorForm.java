package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.ui.MainApp;

import javax.swing.*;

public class CreatorForm extends AbstractForm {

    private final JTextField nameField = new JTextField(20);
    private final JTextArea overviewArea = new JTextArea(5, 20);
    private final CreatorService creatorService = new CreatorService();

    public CreatorForm() {
        super("Add New Creator");
        overviewArea.setLineWrap(true);
        overviewArea.setWrapStyleWord(true);
        addFormField("Name", nameField);
        addFormField("Overview", new JScrollPane(overviewArea));

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
        this();

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