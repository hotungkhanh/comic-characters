package com.tuka.comiccharacters.ui.panel;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.service.PublisherService;

import javax.swing.*;
import java.util.List;

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

        CreatorService creatorService = new CreatorService();

        List<Creator> allCreators = creatorService.getAllCreators();
        JList<Creator> creatorList = new JList<>(allCreators.toArray(new Creator[0]));
        creatorList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(creatorList);

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

        add(new JLabel("Creator:"));
        add(scrollPane);

        JButton addButton = new JButton("Add Character");
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String alias = aliasField.getText();
            Publisher selectedPublisher = (Publisher) publisherDropdown.getSelectedItem();
            List<Creator> selectedCreator = creatorList.getSelectedValuesList();

            if (name.isEmpty()) {
                showError("Character name is required.");
                return;
            }

            service.addCharacter(name, alias, selectedPublisher, selectedCreator);
            showSuccess("Character added!");
            nameField.setText("");
            aliasField.setText("");
        });

        add(addButton);
    }
}
