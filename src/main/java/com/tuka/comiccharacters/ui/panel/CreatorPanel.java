package com.tuka.comiccharacters.ui.panel;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.ui.MainApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CreatorPanel extends JPanel {

    private final JTextField nameField = new JTextField(20);
    private final JTextArea overviewArea = new JTextArea(5, 20);
    private final CreatorService creatorService = new CreatorService();
    private final JButton submitButton = new JButton("Add Creator");

    public CreatorPanel() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 1));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Creator"));

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Overview:"));
        overviewArea.setLineWrap(true);
        overviewArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(overviewArea));

        submitButton.addActionListener(e -> {
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

        add(formPanel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);
    }

    public CreatorPanel(Creator existingCreator) {
        this(); // Set up the layout and fields

        nameField.setText(existingCreator.getName());
        overviewArea.setText(existingCreator.getOverview());

        // Change button text to "Save"
        submitButton.setText("Save");

        // Remove existing listeners (from add mode)
        for (ActionListener al : submitButton.getActionListeners()) {
            submitButton.removeActionListener(al);
        }

        // Add save logic
        submitButton.addActionListener(e -> {
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
