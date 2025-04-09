package com.tuka.comiccharacters.ui.panel;

import com.tuka.comiccharacters.service.CharacterService;

import javax.swing.*;

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class CharacterPanel extends JPanel {
    public CharacterPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Character"));

        JTextField nameField = new JTextField(10);
        JTextField aliasField = new JTextField(10);
        JTextField publisherField = new JTextField(10);
        CharacterService service = new CharacterService();

        add(new JLabel("Name:"));
        add(nameField);
        add(Box.createVerticalStrut(5));
        add(new JLabel("Alias:"));
        add(aliasField);
        add(Box.createVerticalStrut(5));
        add(new JLabel("Publisher:"));
        add(publisherField);
        add(Box.createVerticalStrut(10));

        JButton addButton = new JButton("Add Character");
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            if (name.isEmpty()) {
                showError("Character name is required.");
                return;
            }
            service.addCharacter(name, aliasField.getText(), publisherField.getText());
            showSuccess("Character added!");
            nameField.setText("");
            aliasField.setText("");
            publisherField.setText("");
        });

        add(addButton);
    }
}
