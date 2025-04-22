package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.*;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.service.SeriesService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class CharacterForm extends AbstractForm {

    private final JTextField nameField = new JTextField(20);
    private final JTextField aliasField = new JTextField(20);
    private final JComboBox<Publisher> publisherDropdown;
    private final JTextField creatorSearchField = new JTextField(20);
    private final DefaultListModel<Creator> selectedCreatorsModel = new DefaultListModel<>();
    private final JComboBox<Series> firstAppearanceSeriesDropdown;
    private final JComboBox<Issue> firstAppearanceIssueDropdown;
    private final JTextArea overviewTextArea = new JTextArea(5, 20);
    private final CharacterService characterService = new CharacterService();
    private final CreatorService creatorService = new CreatorService();
    private final SeriesService seriesService = new SeriesService();
    private ComicCharacter editingCharacter;

    public CharacterForm() {
        super("Add New Character");
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        addFormField("Name", nameField);
        addFormField("Alias", aliasField);

        PublisherService publisherService = new PublisherService();
        Set<Publisher> allPublishers = publisherService.getAllPublishers();
        List<Publisher> publishersWithNull = new ArrayList<>();
        publishersWithNull.add(null);
        publishersWithNull.addAll(allPublishers);
        publisherDropdown = new JComboBox<>(publishersWithNull.toArray(new Publisher[0]));
        publisherDropdown.setRenderer(new NullableItemRenderer("None"));
        addFormField("Publisher", publisherDropdown);

        overviewTextArea.setLineWrap(true);
        overviewTextArea.setWrapStyleWord(true);
        addFormField("Overview", new JScrollPane(overviewTextArea));

        JPanel creatorPanel = new JPanel();
        creatorPanel.setLayout(new BoxLayout(creatorPanel, BoxLayout.Y_AXIS));
        creatorPanel.setBorder(BorderFactory.createTitledBorder("Creators (Right click to remove)"));

        // Selected creators list
        JList<Creator> selectedCreatorsList = getSelectedCreatorsList();
        JScrollPane selectedScrollPane = new JScrollPane(selectedCreatorsList);
        selectedScrollPane.setPreferredSize(new Dimension(250, 100));
        creatorPanel.add(selectedScrollPane);

        // Search field
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        creatorSearchField.setPreferredSize(new Dimension(200, 25));
        searchPanel.add(creatorSearchField);
        creatorPanel.add(searchPanel);

        // Search result list and add button panel
        JPanel resultPanel = new JPanel(new BorderLayout());

        DefaultListModel<Creator> searchResultsModel = new DefaultListModel<>();
        JList<Creator> searchResultsList = new JList<>(searchResultsModel);
        searchResultsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane searchScrollPane = new JScrollPane(searchResultsList);
        searchScrollPane.setPreferredSize(new Dimension(250, 50));

        JButton addCreatorButton = new JButton("Add Creator(s)");
        addCreatorButton.setPreferredSize(new Dimension(80, 30));

        resultPanel.add(searchScrollPane, BorderLayout.CENTER);
        resultPanel.add(addCreatorButton, BorderLayout.EAST);
        creatorPanel.add(resultPanel);
        formPanel.add(creatorPanel);

        creatorSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void updateSearch() {
                String search = creatorSearchField.getText().trim().toLowerCase();
                searchResultsModel.clear();
                if (!search.isEmpty()) {
                    Set<Creator> allCreators = creatorService.getAllCreators();
                    List<Creator> matches = allCreators.stream().filter(c -> c.getName().toLowerCase().contains(search)).toList();
                    matches.forEach(searchResultsModel::addElement);
                }
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch();
            }
        });

        addCreatorButton.addActionListener(_ -> {
            List<Creator> selected = searchResultsList.getSelectedValuesList();
            for (Creator creator : selected) {
                if (!selectedCreatorsModel.contains(creator)) {
                    selectedCreatorsModel.addElement(creator);
                }
            }
            searchResultsList.clearSelection();
            creatorSearchField.setText("");
        });

        formPanel.add(creatorPanel);

        Set<Series> allSeries = seriesService.getAllSeries();
        List<Series> seriesWithNull = new ArrayList<>();
        seriesWithNull.add(null);
        seriesWithNull.addAll(allSeries);
        firstAppearanceSeriesDropdown = new JComboBox<>(seriesWithNull.toArray(new Series[0]));
        firstAppearanceSeriesDropdown.setRenderer(new NullableItemRenderer("None"));
        addFormField("First appearance Series", firstAppearanceSeriesDropdown);

        firstAppearanceIssueDropdown = new JComboBox<>();
        firstAppearanceIssueDropdown.setEnabled(false);
        addFormField("First appearance Issue", firstAppearanceIssueDropdown);

        firstAppearanceSeriesDropdown.addActionListener(_ -> populateIssuesDropdown());

        addSubmitListener(_ -> {
            String name = nameField.getText().trim();
            String alias = aliasField.getText().trim();
            Publisher selectedPublisher = (Publisher) publisherDropdown.getSelectedItem();
            String overview = overviewTextArea.getText().trim();
            List<Creator> selectedCreators = new ArrayList<>();
            for (int i = 0; i < selectedCreatorsModel.size(); i++) {
                selectedCreators.add(selectedCreatorsModel.get(i));
            }
            Issue selectedFirstAppearance = (Issue) firstAppearanceIssueDropdown.getSelectedItem();
            Series selectedSeries = (Series) firstAppearanceSeriesDropdown.getSelectedItem();

            if (name.isEmpty()) {
                showError("Character name is required.");
                return;
            }

            if (selectedSeries != null && selectedFirstAppearance == null) {
                showError("You must select an Issue if you select a Series.");
                return;
            }

            characterService.addCharacter(name, alias, selectedPublisher, overview, selectedCreators, selectedFirstAppearance);
            showSuccess("Character added!");
            resetForm();
        });
    }

    public CharacterForm(ComicCharacter character) {
        this();
        setSubmitButtonText("Save Changes");
        this.editingCharacter = character;
        nameField.setText(character.getName());
        aliasField.setText(character.getAlias());
        publisherDropdown.setSelectedItem(character.getPublisher());
        overviewTextArea.setText(character.getOverview());

        for (Creator creator : character.getCreators()) {
            if (!selectedCreatorsModel.contains(creator)) {
                selectedCreatorsModel.addElement(creator);
            }
        }

        firstAppearanceSeriesDropdown.setSelectedItem(character.getFirstAppearance() != null ? character.getFirstAppearance().getSeries() : null);
        populateIssuesDropdown(character.getFirstAppearance());

        removeAllSubmitListeners();
        addSubmitListener(_ -> {
            String name = nameField.getText().trim();
            String alias = aliasField.getText().trim();
            Publisher selectedPublisher = (Publisher) publisherDropdown.getSelectedItem();
            String overview = overviewTextArea.getText().trim();
            List<Creator> selectedCreators = new ArrayList<>();
            for (int i = 0; i < selectedCreatorsModel.size(); i++) {
                selectedCreators.add(selectedCreatorsModel.get(i));
            }
            Issue selectedFirstAppearance = (Issue) firstAppearanceIssueDropdown.getSelectedItem();
            Series selectedSeries = (Series) firstAppearanceSeriesDropdown.getSelectedItem();

            if (name.isEmpty()) {
                showError("Character name is required.");
                return;
            }

            if (selectedSeries != null && selectedFirstAppearance == null) {
                showError("You must select an Issue if you select a Series.");
                return;
            }

            editingCharacter.setName(name);
            editingCharacter.setAlias(alias);
            editingCharacter.setPublisher(selectedPublisher);
            editingCharacter.setOverview(overview);
            editingCharacter.setCreators(new HashSet<>(selectedCreators));
            editingCharacter.setFirstAppearance(selectedFirstAppearance);

            characterService.updateCharacter(editingCharacter);
            showSuccess("Character updated!");
            SwingUtilities.getWindowAncestor(this).dispose();
        });
    }

    private JList<Creator> getSelectedCreatorsList() {
        JList<Creator> selectedCreatorsList = new JList<>(selectedCreatorsModel);
        selectedCreatorsList.setVisibleRowCount(5);
        selectedCreatorsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        selectedCreatorsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = selectedCreatorsList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        selectedCreatorsModel.removeElementAt(index);
                    }
                }
            }
        });
        return selectedCreatorsList;
    }

    private void populateIssuesDropdown() {
        Series selectedSeries = (Series) firstAppearanceSeriesDropdown.getSelectedItem();
        firstAppearanceIssueDropdown.removeAllItems();
        firstAppearanceIssueDropdown.setEnabled(selectedSeries != null);
        if (selectedSeries != null) {
            List<Issue> issues = seriesService.getIssuesBySeries(selectedSeries);
            for (Issue issue : issues) {
                firstAppearanceIssueDropdown.addItem(issue);
            }
        } else {
            firstAppearanceIssueDropdown.addItem(null);
            firstAppearanceIssueDropdown.setRenderer(new NullableItemRenderer("None"));
        }
    }

    private void populateIssuesDropdown(Issue preselectedIssue) {
        populateIssuesDropdown();
        firstAppearanceIssueDropdown.setSelectedItem(preselectedIssue);
    }

    private void resetForm() {
        nameField.setText("");
        aliasField.setText("");
        publisherDropdown.setSelectedIndex(0);
        overviewTextArea.setText("");
        selectedCreatorsModel.clear();
        firstAppearanceSeriesDropdown.setSelectedIndex(0);
        firstAppearanceIssueDropdown.removeAllItems();
        firstAppearanceIssueDropdown.setEnabled(false);
        firstAppearanceIssueDropdown.addItem(null);
        firstAppearanceIssueDropdown.setRenderer(new NullableItemRenderer("None"));
    }

    private static class NullableItemRenderer extends DefaultListCellRenderer {
        private final String nullText;

        public NullableItemRenderer(String nullText) {
            this.nullText = nullText;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setText(value == null ? nullText : value.toString());
            return this;
        }
    }
}