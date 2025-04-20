package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.*;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.service.IssueService;
import com.tuka.comiccharacters.service.SeriesService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class IssueForm extends JPanel {

    private final JComboBox<Series> seriesDropdown;
    private final JTextField issueNumberField;
    private final JComboBox<Creator> creatorDropdown;
    private final JList<Role> roleList;
    private final DefaultTableModel creatorTableModel;
    private final List<IssueCreator> selectedCreators = new ArrayList<>();
    private final JList<ComicCharacter> characterList;

    private final SeriesService seriesService = new SeriesService();
    private final CharacterService characterService = new CharacterService();
    private final IssueService issueService = new IssueService();

    public IssueForm() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Issue"));

        seriesDropdown = new JComboBox<>(seriesService.getAllSeries().toArray(new Series[0]));
        issueNumberField = new JTextField(10);

        CreatorService creatorService = new CreatorService();
        creatorDropdown = new JComboBox<>(creatorService.getAllCreators().toArray(new Creator[0]));
        roleList = new JList<>(Role.values());
        roleList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane roleScroll = new JScrollPane(roleList);

        characterList = new JList<>();
        characterList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        characterList.setVisibleRowCount(5);
        refreshCharacters();
        JScrollPane characterScroll = new JScrollPane(characterList);

        creatorTableModel = new DefaultTableModel(new Object[]{"Creator", "Roles"}, 0);
        JTable creatorTable = new JTable(creatorTableModel);

        // Build UI
        addLabeledComponent("Select Series:", seriesDropdown);
        addLabeledComponent("Issue Number:", issueNumberField);
        addLabeledComponent("Select Creator:", creatorDropdown);
        addLabeledComponent("Select Role(s):", roleScroll);

        JButton addCreatorButton = new JButton("Add Creator & Roles");
        addCreatorButton.addActionListener(e -> addCreatorWithRoles());
        add(addCreatorButton);
        add(Box.createVerticalStrut(10));

        add(new JLabel("Selected Creators & Roles:"));
        add(new JScrollPane(creatorTable));
        add(Box.createVerticalStrut(10));

        addLabeledComponent("Select Characters:", characterScroll);

        JButton addButton = new JButton("Add Issue");
        addButton.addActionListener(e -> addIssue());
        add(addButton);
    }

    private void addLabeledComponent(String label, JComponent component) {
        add(new JLabel(label));
        add(component);
        add(Box.createVerticalStrut(5));
    }

    private void addCreatorWithRoles() {
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
    }

    private void addIssue() {
        Series selectedSeries = (Series) seriesDropdown.getSelectedItem();
        String issueText = issueNumberField.getText().trim();
        List<ComicCharacter> selectedCharacters = characterList.getSelectedValuesList();

        if (selectedSeries == null || issueText.isEmpty()) {
            showError("Please select a series and enter an issue number.");
            return;
        }

        try {
            BigDecimal number = new BigDecimal(issueText);
            issueService.addIssue(selectedSeries, number, selectedCreators, selectedCharacters);
            showSuccess("Issue added with creators and characters!");
            resetForm();
        } catch (NumberFormatException ex) {
            showError("Issue number must be a number.");
        }
    }

    private void resetForm() {
        issueNumberField.setText("");
        creatorTableModel.setRowCount(0);
        selectedCreators.clear();
        characterList.clearSelection();
    }

    public void refreshSeries() {
        seriesDropdown.removeAllItems();
        for (Series s : seriesService.getAllSeries()) {
            seriesDropdown.addItem(s);
        }
    }

    public void refreshCharacters() {
        List<ComicCharacter> updatedCharacters = characterService.getAllCharacters();
        characterList.setListData(updatedCharacters.toArray(new ComicCharacter[0]));
    }
}
