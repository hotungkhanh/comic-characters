package com.tuka.comiccharacters.ui.panel;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.service.IssueService;
import com.tuka.comiccharacters.service.PublisherService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class CharacterPanel extends JPanel {
    public CharacterPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Character"));

        JTextField nameField = new JTextField(10);
        JTextField aliasField = new JTextField(10);

        PublisherService publisherService = new PublisherService();
        List<Publisher> allPublishers = publisherService.getAllPublishers();
        List<Publisher> publishersWithNull = new ArrayList<>();
        publishersWithNull.add(null); // represents 'None'
        publishersWithNull.addAll(allPublishers);

        JComboBox<Publisher> publisherDropdown = new JComboBox<>(publishersWithNull.toArray(new Publisher[0]));
        publisherDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                   boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "None" : value.toString());
                return this;
            }
        });

        // Creator list
        CreatorService creatorService = new CreatorService();
        List<Creator> allCreators = creatorService.getAllCreators();
        JList<Creator> creatorList = new JList<>(allCreators.toArray(new Creator[0]));
        creatorList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(creatorList);

        // First Appearance dropdown with null option
        IssueService issueService = new IssueService();
        List<Issue> allIssues = issueService.getAllIssues();
        List<Issue> issuesWithNull = new ArrayList<>();
        issuesWithNull.add(null); // represents 'None'
        issuesWithNull.addAll(allIssues);

        JComboBox<Issue> issueDropdown = new JComboBox<>(issuesWithNull.toArray(new Issue[0]));
        issueDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                   boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "None" : value.toString());
                return this;
            }
        });

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

        add(new JLabel("Creator(s):"));
        add(scrollPane);
        add(Box.createVerticalStrut(10));

        add(new JLabel("First Appearance:"));
        add(issueDropdown);
        add(Box.createVerticalStrut(10));

        JButton addButton = new JButton("Add Character");
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String alias = aliasField.getText().trim();
            Publisher selectedPublisher = (Publisher) publisherDropdown.getSelectedItem(); // can be null
            List<Creator> selectedCreators = creatorList.getSelectedValuesList();
            Issue selectedIssue = (Issue) issueDropdown.getSelectedItem(); // can be null

            if (name.isEmpty()) {
                showError("Character name is required.");
                return;
            }

            service.addCharacter(name, alias, selectedPublisher, selectedCreators, selectedIssue);
            showSuccess("Character added!");

            // Reset form
            nameField.setText("");
            aliasField.setText("");
            publisherDropdown.setSelectedIndex(0);
            creatorList.clearSelection();
            issueDropdown.setSelectedIndex(0);
        });

        add(addButton);
    }
}
