package com.tuka.comiccharacters.ui.panel;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.model.IssueCreator;
import com.tuka.comiccharacters.model.Role;
import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.service.IssueService;
import com.tuka.comiccharacters.service.SeriesService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class IssuePanel extends JPanel {

    public IssuePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Issue"));

        SeriesService seriesService = new SeriesService();
        CreatorService creatorService = new CreatorService();
        IssueService issueService = new IssueService();

        JComboBox<Series> seriesDropdown = new JComboBox<>(seriesService.getAllSeries().toArray(new Series[0]));
        JTextField issueNumberField = new JTextField(10);

        JComboBox<Creator> creatorDropdown = new JComboBox<>(creatorService.getAllCreators().toArray(new Creator[0]));
        JList<Role> roleList = new JList<>(Role.values());
        roleList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        roleList.setVisibleRowCount(3);
        JScrollPane roleScroll = new JScrollPane(roleList);

        DefaultTableModel creatorTableModel = new DefaultTableModel(new Object[]{"Creator", "Roles"}, 0);
        JTable creatorTable = new JTable(creatorTableModel);

        List<IssueCreator> selectedCreators = new ArrayList<>();

        add(new JLabel("Select Series:"));
        add(seriesDropdown);
        add(Box.createVerticalStrut(5));

        add(new JLabel("Issue Number:"));
        add(issueNumberField);
        add(Box.createVerticalStrut(10));

        add(new JLabel("Select Creator:"));
        add(creatorDropdown);
        add(Box.createVerticalStrut(5));

        add(new JLabel("Select Role(s):"));
        add(roleScroll);
        add(Box.createVerticalStrut(5));

        JButton addCreatorButton = new JButton("Add Creator & Roles");
        addCreatorButton.addActionListener(e -> {
            Creator creator = (Creator) creatorDropdown.getSelectedItem();
            List<Role> roles = roleList.getSelectedValuesList();

            if (creator == null || roles.isEmpty()) {
                showError("Please select a creator and at least one role.");
                return;
            }

            IssueCreator issueCreator = new IssueCreator();
            issueCreator.setCreator(creator);
            issueCreator.setRoles(new HashSet<>(roles));
            selectedCreators.add(issueCreator);

            String roleNames = String.join(", ", roles.stream().map(Enum::name).toList());
            creatorTableModel.addRow(new Object[]{creator.getName(), roleNames});

            roleList.clearSelection();
        });
        add(addCreatorButton);
        add(Box.createVerticalStrut(10));

        add(new JLabel("Selected Creators & Roles:"));
        add(new JScrollPane(creatorTable));
        add(Box.createVerticalStrut(10));

        JButton addButton = new JButton("Add Issue");
        addButton.addActionListener(e -> {
            Series selectedSeries = (Series) seriesDropdown.getSelectedItem();
            String issueText = issueNumberField.getText();

            if (selectedSeries == null || issueText.isEmpty()) {
                showError("Please select a series and enter an issue number.");
                return;
            }

            try {
                int number = Integer.parseInt(issueText);
                issueService.addIssue(selectedSeries, number, selectedCreators);
                showSuccess("Issue added with creators!");
                issueNumberField.setText("");
                creatorTableModel.setRowCount(0);
                selectedCreators.clear();
            } catch (NumberFormatException ex) {
                showError("Issue number must be a number.");
            }
        });

        add(addButton);
    }
}
