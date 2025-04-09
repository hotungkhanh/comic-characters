package com.tuka.comiccharacters.ui.panel;

import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.PublisherService;

import javax.swing.*;

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class CharacterPanel extends JPanel {
    public CharacterPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Character"));

        JTextField nameField = new JTextField(10);
        JTextField aliasField = new JTextField(10);
        JComboBox<Publisher> publisherDropdown = new JComboBox<>(
                new PublisherService().getAllPublishers().toArray(new Publisher[0])
        );

        CharacterService service = new CharacterService();

        add(new JLabel("Name:"));
        add(nameField);
        add(Box.createVerticalStrut(5));

        add(new JLabel("Alias:"));
        add(aliasField);
        add(Box.createVerticalStrut(5));

        add(new JLabel("Publisher:"));
        add(publisherDropdown);
        add(Box.createVerticalStrut(10));

        JButton addButton = new JButton("Add Character");
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String alias = aliasField.getText();
            Publisher selectedPublisher = (Publisher) publisherDropdown.getSelectedItem();

            if (name.isEmpty()) {
                showError("Character name is required.");
                return;
            }

            service.addCharacter(name, alias, selectedPublisher);
            showSuccess("Character added!");
            nameField.setText("");
            aliasField.setText("");
        });

        add(addButton);
    }
}
