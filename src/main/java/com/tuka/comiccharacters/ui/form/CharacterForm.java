package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.*;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.service.SeriesService;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CharacterForm extends AbstractForm {
    // Form fields
    private final JTextField nameField = new JTextField(20);
    private final JTextField aliasField = new JTextField(20);
    private final JTextField imageUrlField = new JTextField(20);
    private final JTextArea overviewTextArea = new JTextArea(5, 20);
    // Services - initialized once
    private final CharacterService characterService = new CharacterService();
    private final CreatorService creatorService = new CreatorService();
    private final SeriesService seriesService = new SeriesService();
    private final PublisherService publisherService = new PublisherService();
    private JComboBox<Publisher> publisherDropdown;
    // First appearance dropdowns
    private JComboBox<Series> seriesDropdown;
    private JComboBox<Issue> issueDropdown;
    private JPanel issueDropdownPanel;
    // Component panels
    private CreatorSelectionPanel creatorSelectionPanel;
    // State management
    private ComicCharacter editingCharacter;

    /**
     * Creates a new character form for adding characters
     */
    public CharacterForm() {
        super("Add New Character");
        buildUI();
        loadInitialData();
        setupSubmitAction();
    }

    /**
     * Creates a new character form for editing an existing character
     *
     * @param character The character to edit
     */
    public CharacterForm(ComicCharacter character) {
        super("Edit Character");
        this.editingCharacter = character;
        setEditMode(true);
        buildUI();
        loadInitialData();
        populateFields(character);
        setupEditAction();
    }

    @Override
    protected void buildUI() {
        int row = 0;

        // Character basic info section
        row = addTextField("Name:", nameField, row, true);
        row = addTextField("Alias:", aliasField, row, false);

        // Add image URL field
        row = addTextField("Image URL:", imageUrlField, row, false);

        // Publisher dropdown
        Set<Publisher> allPublishers = publisherService.getAllEntities();
        List<Publisher> publishersWithNull = new ArrayList<>();
        publishersWithNull.add(null);
        publishersWithNull.addAll(allPublishers);
        publisherDropdown = createNullableDropdown(publishersWithNull.toArray(new Publisher[0]), "None");
        row = addDropdown("Publisher:", publisherDropdown, row, false);

        // Overview text area
        row = addTextArea("Overview:", overviewTextArea, row, 5, false);

        // Creators section
        creatorSelectionPanel = new CreatorSelectionPanel(creatorService);
        row = addPanel(creatorSelectionPanel, row);

        // First appearance section
        JPanel firstAppearancePanel = createFirstAppearancePanel();
        row = addPanel(firstAppearancePanel, row);
    }

    /**
     * Creates the panel for selecting a character's first appearance
     *
     * @return The first appearance panel
     */
    private JPanel createFirstAppearancePanel() {
        JPanel panel = createTitledPanel("First Appearance");
        panel.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createDefaultConstraints();

        // Series dropdown
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Series:"), gbc);

        // Get all series and sort them alphabetically
        Set<Series> allSeriesSet = seriesService.getAllEntities();
        List<Series> allSeriesList = new ArrayList<>(allSeriesSet);

        // Sort series by title for better usability
        allSeriesList.sort(Comparator.comparing(Series::getTitle));

        // Add null option at the beginning
        List<Series> seriesWithNull = new ArrayList<>();
        seriesWithNull.add(null);
        seriesWithNull.addAll(allSeriesList);

        seriesDropdown = createNullableDropdown(seriesWithNull.toArray(new Series[0]), "Select a Series");

        gbc.gridx = 1;
        contentPanel.add(seriesDropdown, gbc);

        // Issue dropdown (initially not visible until Series is selected)
        issueDropdownPanel = new JPanel(new GridBagLayout());
        GridBagConstraints issueGbc = createDefaultConstraints();

        issueGbc.gridx = 0;
        issueGbc.gridy = 0;
        issueDropdownPanel.add(new JLabel("Issue:"), issueGbc);

        issueDropdown = new JComboBox<>();
        issueDropdown.setEnabled(false);

        issueGbc.gridx = 1;
        issueDropdownPanel.add(issueDropdown, issueGbc);

        // Initially hide the issue dropdown panel
        issueDropdownPanel.setVisible(false);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        contentPanel.add(issueDropdownPanel, gbc);

        // Add listener to update issues when series changes
        seriesDropdown.addActionListener(e -> {
            Series selectedSeries = (Series) seriesDropdown.getSelectedItem();
            updateIssueDropdown(selectedSeries);
        });

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Updates the issue dropdown based on the selected series
     *
     * @param series The selected series
     */
    private void updateIssueDropdown(Series series) {
        issueDropdown.removeAllItems();

        if (series == null) {
            issueDropdownPanel.setVisible(false);
            issueDropdown.setEnabled(false);
            return;
        }

        // Load issues for the selected series
        List<Issue> issues = seriesService.getIssuesBySeries(series);

        // Sort issues by issue number for better usability
        issues.sort(Comparator.comparing(Issue::getIssueNumber));

        if (issues.isEmpty()) {
            issueDropdown.setEnabled(false);
        } else {
            issueDropdown.setEnabled(true);
            for (Issue issue : issues) {
                issueDropdown.addItem(issue);
            }
        }

        // Make issue dropdown panel visible
        issueDropdownPanel.setVisible(true);

        // Revalidate and repaint to ensure UI updates correctly
        issueDropdownPanel.revalidate();
        issueDropdownPanel.repaint();
    }

    /**
     * Sets up the submit action for adding new characters
     */
    private void setupSubmitAction() {
        addSubmitListener(e -> {
            if (!validateForm()) {
                return;
            }
            ComicCharacter character = collectFormData();
            // Save the character
            characterService.save(character);
            showSuccess("Character added!");
            resetForm();
        });
    }

    /**
     * Sets up the submit action for editing characters
     */
    private void setupEditAction() {
        removeAllSubmitListeners();
        addSubmitListener(e -> {
            if (!validateForm()) {
                return;
            }
            ComicCharacter characterData = collectFormData();
            editingCharacter.setName(characterData.getName());
            editingCharacter.setAlias(characterData.getAlias());
            editingCharacter.setPublisher(characterData.getPublisher());
            editingCharacter.setOverview(characterData.getOverview());
            editingCharacter.setImageUrl(characterData.getImageUrl().isEmpty() ? null : characterData.getImageUrl());
            editingCharacter.setCreators(characterData.getCreators());
            editingCharacter.setFirstAppearance(characterData.getFirstAppearance());

            characterService.save(editingCharacter);
            showSuccess("Character updated!");
            SwingUtilities.getWindowAncestor(this).dispose();
        });
    }

    /**
     * Helper method to collect all form data into a single object
     */
    private ComicCharacter collectFormData() {
        ComicCharacter character = new ComicCharacter();
        character.setName(nameField.getText().trim());
        character.setAlias(aliasField.getText().trim());
        character.setImageUrl(imageUrlField.getText().trim());
        character.setPublisher((Publisher) publisherDropdown.getSelectedItem());
        character.setOverview(overviewTextArea.getText().trim());
        character.setCreators(new HashSet<>(creatorSelectionPanel.getSelectedCreators()));
        character.setFirstAppearance((Issue) issueDropdown.getSelectedItem());
        return character;
    }

    /**
     * Validates the form fields
     *
     * @return Whether the form is valid
     */
    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Character name is required.");
            nameField.requestFocus();
            return false;
        }

        // Validate first appearance - if series is selected, issue must also be selected
        Series selectedSeries = (Series) seriesDropdown.getSelectedItem();
        Issue selectedIssue = (Issue) issueDropdown.getSelectedItem();

        if (selectedSeries != null && selectedIssue == null) {
            showError("Please select an issue for the chosen series or clear the series selection.");
            issueDropdown.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Populates form fields with data from an existing character
     *
     * @param character The character to load data from
     */
    private void populateFields(ComicCharacter character) {
        nameField.setText(character.getName());
        aliasField.setText(character.getAlias() != null ? character.getAlias() : "");
        imageUrlField.setText(character.getImageUrl() != null ? character.getImageUrl() : "");
        publisherDropdown.setSelectedItem(character.getPublisher());
        overviewTextArea.setText(character.getOverview() != null ? character.getOverview() : "");

        creatorSelectionPanel.setSelectedCreators(character.getCreators());

        // Set first appearance if it exists
        Issue firstAppearance = character.getFirstAppearance();
        if (firstAppearance != null) {
            Series series = firstAppearance.getSeries();
            seriesDropdown.setSelectedItem(series);
            // Series selection will trigger issue dropdown update
            // But we need to set the selected issue after that happens
            SwingUtilities.invokeLater(() -> issueDropdown.setSelectedItem(firstAppearance));
        }
    }

    /**
     * Loads initial data for the form
     */
    private void loadInitialData() {
        creatorSelectionPanel.loadCreators();
    }

    @Override
    protected void resetForm() {
        nameField.setText("");
        aliasField.setText("");
        imageUrlField.setText("");
        publisherDropdown.setSelectedIndex(0);
        overviewTextArea.setText("");
        creatorSelectionPanel.clearSelection();
        seriesDropdown.setSelectedIndex(0);
        issueDropdown.removeAllItems();
        issueDropdownPanel.setVisible(false);
    }

    /**
     * Panel for creator selection functionality
     */
    private class CreatorSelectionPanel extends JPanel {
        private final JTextField creatorSearchField;
        private final DefaultListModel<Creator> selectedCreatorsModel = new DefaultListModel<>();
        private final DefaultListModel<Creator> searchResultsModel = new DefaultListModel<>();
        private final JList<Creator> selectedCreatorsList;
        private final JList<Creator> searchResultsList;
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        private final CreatorService creatorService;
        private ScheduledFuture<?> creatorSearchTask;
        private Set<Creator> allCreators = new HashSet<>();

        public CreatorSelectionPanel(CreatorService creatorService) {
            super(new BorderLayout());
            this.creatorService = creatorService;

            setBorder(BorderFactory.createTitledBorder("Creators"));

            // Setup creator search field
            creatorSearchField = createSearchField("Search for Creators...");
            JPanel creatorSearchPanel = new JPanel(new BorderLayout());
            creatorSearchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
            creatorSearchPanel.add(creatorSearchField, BorderLayout.CENTER);

            // Setup search results
            searchResultsList = new JList<>(searchResultsModel);
            searchResultsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            JScrollPane searchScrollPane = new JScrollPane(searchResultsList);
            searchScrollPane.setPreferredSize(new Dimension(300, 80));

            // Add Creator button
            JButton addCreatorButton = new JButton("Add Creator(s)");
            addCreatorButton.setPreferredSize(new Dimension(300, 30));
            JPanel addCreatorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            addCreatorPanel.add(addCreatorButton);

            // Setup selected creators list
            selectedCreatorsList = new JList<>(selectedCreatorsModel);
            JScrollPane selectedScrollPane = new JScrollPane(selectedCreatorsList);
            selectedScrollPane.setPreferredSize(new Dimension(300, 80));

            // Combine panels
            JPanel searchAndAddPanel = new JPanel(new BorderLayout());
            searchAndAddPanel.add(searchScrollPane, BorderLayout.CENTER);
            searchAndAddPanel.add(addCreatorPanel, BorderLayout.SOUTH);

            add(creatorSearchPanel, BorderLayout.NORTH);
            add(searchAndAddPanel, BorderLayout.CENTER);
            add(selectedScrollPane, BorderLayout.SOUTH);

            // Add event listeners
            addCreatorRemovalListener();
            addCreatorButton.addActionListener(e -> addSelectedCreators());
            setupCreatorSearchListener();
        }

        public void loadCreators() {
            Executors.newSingleThreadExecutor().submit(() -> {
                allCreators = creatorService.getAllEntities();
            });
        }

        public List<Creator> getSelectedCreators() {
            List<Creator> result = new ArrayList<>();
            for (int i = 0; i < selectedCreatorsModel.getSize(); i++) {
                result.add(selectedCreatorsModel.getElementAt(i));
            }
            return result;
        }

        public void setSelectedCreators(Set<Creator> creators) {
            selectedCreatorsModel.clear();
            for (Creator creator : creators) {
                selectedCreatorsModel.addElement(creator);
            }
        }

        public void clearSelection() {
            selectedCreatorsModel.clear();
        }

        private void setupCreatorSearchListener() {
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

        private void addSelectedCreators() {
            List<Creator> selected = searchResultsList.getSelectedValuesList();
            for (Creator creator : selected) {
                if (!selectedCreatorsModel.contains(creator)) {
                    selectedCreatorsModel.addElement(creator);
                }
            }
            searchResultsList.clearSelection();
            creatorSearchField.setText("");
        }

        private void addCreatorRemovalListener() {
            addItemRemovalListener(selectedCreatorsList, selectedCreatorsModel, "Remove Creator", null);
        }
    }
}
