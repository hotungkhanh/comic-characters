package com.tuka.comiccharacters.ui.panel;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.ui.MainApp;

import javax.swing.*;
import java.awt.*;

public class CreatorPanel extends JPanel {

    private final JTextField nameField = new JTextField(20);
    private final JTextArea overviewArea = new JTextArea(5, 20);
    private final CreatorService creatorService = new CreatorService();

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

        JButton submitButton = new JButton("Add Creator");
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
}
