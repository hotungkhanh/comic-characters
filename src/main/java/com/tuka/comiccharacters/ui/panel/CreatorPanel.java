package com.tuka.comiccharacters.ui.panel;

import com.tuka.comiccharacters.service.CreatorService;

import javax.swing.*;

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class CreatorPanel extends JPanel {
    public CreatorPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Creator"));

        JTextField nameField = new JTextField(15);
        JTextArea overviewArea = new JTextArea(5, 15);
        overviewArea.setLineWrap(true);
        overviewArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(overviewArea);

        CreatorService service = new CreatorService();

        add(new JLabel("Name:"));
        add(nameField);
        add(Box.createVerticalStrut(5));

        add(new JLabel("Overview:"));
        add(scrollPane);
        add(Box.createVerticalStrut(10));

        JButton addButton = new JButton("Add Creator");
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String overview = overviewArea.getText();

            if (name.isEmpty()) {
                showError("Name is required.");
                return;
            }

            service.addCreator(name, overview);
            showSuccess("Creator added!");
            nameField.setText("");
            overviewArea.setText("");
        });

        add(addButton);
    }
}
