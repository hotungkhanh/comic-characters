package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.*;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.service.SeriesService;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class CharacterForm extends AbstractForm {

    private final JTextField nameField = new JTextField(20);
    private final JTextField aliasField = new JTextField(20);
    private final JComboBox<Publisher> publisherDropdown;
    private final JTextField creatorSearchField = new JTextField(15);
    private final DefaultListModel<Creator> selectedCreatorsModel = new DefaultListModel<>();
    private final JList<Creator> selectedCreatorsList = new JList<>(selectedCreatorsModel);
    private final JComboBox<Series> firstAppearanceSeriesDropdown;
    private final JComboBox<Issue> firstAppearanceIssueDropdown;
    private final JTextArea overviewTextArea = new JTextArea(5, 20);
    private final CharacterService characterService = new CharacterService();
    private final CreatorService creatorService = new CreatorService();
    private final SeriesService seriesService = new SeriesService();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ComicCharacter editingCharacter;
    private ScheduledFuture<?> creatorSearchTask;
    private Set<Creator> allCreators = new HashSet<>();
    private DefaultListModel<Creator> searchResultsModel; // Added field

    public CharacterForm() {
        super("Add New Character");
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JPanel characterInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        characterInfoPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        characterInfoPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        characterInfoPanel.add(new JLabel("Alias:"), gbc);
        gbc.gridx = 1;
        characterInfoPanel.add(aliasField, gbc);

        PublisherService publisherService = new PublisherService();
        Set<Publisher> allPublishers = publisherService.getAllPublishers();
        List<Publisher> publishersWithNull = new ArrayList<>();
        publishersWithNull.add(null);
        publishersWithNull.addAll(allPublishers);
        publisherDropdown = new JComboBox<>(publishersWithNull.toArray(new Publisher[0]));
        publisherDropdown.setRenderer(new NullableItemRenderer("None"));
        gbc.gridx = 0;
        gbc.gridy = 2;
        characterInfoPanel.add(new JLabel("Publisher:"), gbc);
        gbc.gridx = 1;
        characterInfoPanel.add(publisherDropdown, gbc);

        overviewTextArea.setLineWrap(true);
        overviewTextArea.setWrapStyleWord(true);
        JScrollPane overviewScrollPane = new JScrollPane(overviewTextArea);
        overviewScrollPane.setPreferredSize(new Dimension(300, 120));
        gbc.gridx = 0;
        gbc.gridy = 3;
        characterInfoPanel.add(new JLabel("Overview:"), gbc);
        gbc.gridx = 1;
        characterInfoPanel.add(overviewScrollPane, gbc);

        contentPanel.add(characterInfoPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Creators Section
        JPanel creatorPanel = new JPanel(new BorderLayout());
        creatorPanel.setBorder(BorderFactory.createTitledBorder("Creators"));

        // Search Panel
        JPanel creatorSearchPanel = new JPanel(new BorderLayout());
        creatorSearchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        creatorSearchField.setText("Search for Creators...");
        creatorSearchField.setForeground(Color.GRAY);
        creatorSearchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (creatorSearchField.getText().equals("Search for Creators...")) {
                    creatorSearchField.setText("");
                    creatorSearchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (creatorSearchField.getText().isEmpty()) {
                    creatorSearchField.setForeground(Color.GRAY);
                    creatorSearchField.setText("Search for Creators...");
                }
            }
        });
        creatorSearchPanel.add(creatorSearchField, BorderLayout.CENTER);
        creatorPanel.add(creatorSearchPanel, BorderLayout.NORTH);

        // Search results list
        searchResultsModel = new DefaultListModel<>();
        JList<Creator> searchResultsList = new JList<>(searchResultsModel);
        searchResultsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane searchScrollPane = new JScrollPane(searchResultsList);
        searchScrollPane.setPreferredSize(new Dimension(300, 80));

        // Add Creator button
        JPanel addCreatorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addCreatorButton = new JButton("Add Creator(s)");
        addCreatorButton.setPreferredSize(new Dimension(300, 30));
        addCreatorPanel.add(addCreatorButton);
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

        // Selected creators list
        JScrollPane selectedScrollPane = new JScrollPane(selectedCreatorsList);
        selectedScrollPane.setPreferredSize(new Dimension(300, 80));
        creatorPanel.add(selectedScrollPane, BorderLayout.SOUTH);
        addCreatorRemovalListener();

        JPanel searchAndAddPanel = new JPanel(new BorderLayout());
        searchAndAddPanel.add(searchScrollPane, BorderLayout.CENTER);
        searchAndAddPanel.add(addCreatorPanel, BorderLayout.SOUTH);
        creatorPanel.add(searchAndAddPanel, BorderLayout.CENTER);

        creatorSearchField.getDocument().addDocumentListener(new DocumentListener() {
            void updateSearch() {
                scheduleCreatorSearch();
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

        contentPanel.add(creatorPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // First Appearance Series and Issue Dropdowns
        JPanel firstAppearancePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcAppearance = new GridBagConstraints();
        gbcAppearance.anchor = GridBagConstraints.WEST;
        gbcAppearance.insets = new Insets(5, 5, 5, 5);
        gbcAppearance.fill = GridBagConstraints.HORIZONTAL;
        gbcAppearance.weightx = 1.0;

        Set<Series> allSeries = seriesService.getAllSeries();
        List<Series> seriesWithNull = new ArrayList<>();
        seriesWithNull.add(null);
        seriesWithNull.addAll(allSeries);
        firstAppearanceSeriesDropdown = new JComboBox<>(seriesWithNull.toArray(new Series[0]));
        firstAppearanceSeriesDropdown.setRenderer(new NullableItemRenderer("None"));
        gbcAppearance.gridx = 0;
        gbcAppearance.gridy = 0;
        firstAppearancePanel.add(new JLabel("First appearance Series:"), gbcAppearance);
        gbcAppearance.gridx = 1;
        firstAppearancePanel.add(firstAppearanceSeriesDropdown, gbcAppearance);

        firstAppearanceIssueDropdown = new JComboBox<>();
        firstAppearanceIssueDropdown.setEnabled(false);
        firstAppearanceIssueDropdown.setRenderer(new NullableItemRenderer("None"));
        gbcAppearance.gridx = 0;
        gbcAppearance.gridy = 1;
        firstAppearancePanel.add(new JLabel("First appearance Issue:"), gbcAppearance);
        gbcAppearance.gridx = 1;
        firstAppearancePanel.add(firstAppearanceIssueDropdown, gbcAppearance);

        firstAppearanceSeriesDropdown.addActionListener(_ -> populateIssuesDropdown());
        contentPanel.add(firstAppearancePanel);
        contentPanel.add(Box.createVerticalStrut(15));

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);

        // Load initial data for creator search.
        loadInitialData();

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

            if (editingCharacter != null) {
                updateCharacter(name, alias, selectedPublisher, overview, selectedCreators, selectedFirstAppearance);
            } else {
                addCharacter(name, alias, selectedPublisher, overview, selectedCreators, selectedFirstAppearance);
            }
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

            updateCharacter(name, alias, selectedPublisher, overview, selectedCreators, selectedFirstAppearance);
        });
    }

    private void addCharacter(String name, String alias, Publisher selectedPublisher, String overview, List<Creator> selectedCreators, Issue selectedFirstAppearance) {
        characterService.addCharacter(name, alias, selectedPublisher, overview, selectedCreators, selectedFirstAppearance);
        showSuccess("Character added!");
        resetForm();
    }

    private void updateCharacter(String name, String alias, Publisher selectedPublisher, String overview, List<Creator> selectedCreators, Issue selectedFirstAppearance) {
        editingCharacter.setName(name);
        editingCharacter.setAlias(alias);
        editingCharacter.setPublisher(selectedPublisher);
        editingCharacter.setOverview(overview);
        editingCharacter.setCreators(new HashSet<>(selectedCreators));
        editingCharacter.setFirstAppearance(selectedFirstAppearance);
        characterService.updateCharacter(editingCharacter);
        showSuccess("Character updated!");
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void addCreatorRemovalListener() {
        selectedCreatorsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = selectedCreatorsList.locationToIndex(e.getPoint());
                    if (index >= 0 && index < selectedCreatorsModel.getSize()) {
                        selectedCreatorsList.setSelectedIndex(index);
                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem removeItem = new JMenuItem("Remove Creator");
                        removeItem.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent event) {
                                removeSelectedCreator();
                            }
                        });
                        popupMenu.add(removeItem);
                        popupMenu.show(selectedCreatorsList, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private void removeSelectedCreator() {
        int selectedIndex = selectedCreatorsList.getSelectedIndex();
        if (selectedIndex != -1) {
            selectedCreatorsModel.remove(selectedIndex);
        }
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

    private void scheduleCreatorSearch() {
        if (creatorSearchTask != null) {
            creatorSearchTask.cancel(true);
        }
        long SEARCH_DELAY = 300;
        creatorSearchTask = scheduler.schedule(() -> {
            String search = creatorSearchField.getText().trim().toLowerCase();
            SwingUtilities.invokeLater(() -> {
                searchResultsModel.clear();
                allCreators.stream()
                        .filter(creator -> creator.getName().toLowerCase().contains(search))
                        .forEach(searchResultsModel::addElement);
            });
        }, SEARCH_DELAY, TimeUnit.MILLISECONDS);
    }

    private void loadInitialData() {
        Executors.newSingleThreadExecutor().submit(() -> {
            allCreators = creatorService.getAllCreators();
        });
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
