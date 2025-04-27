package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.*;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.service.IssueService;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

public class IssueForm extends AbstractForm {

    // Form fields
    private final JTextField issueNumberField = new JTextField(10);
    private final JTextArea overviewTextArea = new JTextArea(3, 20);
    private final JTextField releaseDateField = new JTextField(10);
    private final JTextField priceField = new JTextField(10);
    private final JTextField imageUrlField = new JTextField(30);
    private final JCheckBox annualCheckBox = new JCheckBox("Annual Issue");

    // Creators section
    private final DefaultTableModel creatorTableModel = new DefaultTableModel(new Object[]{"Name", "Role(s)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable creatorTable = new JTable(creatorTableModel);
    private final JList<Role> roleSearchList = new JList<>(Role.values());
    private final DefaultListModel<Creator> matchedCreatorsListModel = new DefaultListModel<>();
    private final JList<Creator> matchedCreatorsList = new JList<>(matchedCreatorsListModel);
    private final List<IssueCreator> selectedCreators = new ArrayList<>();

    // Characters section
    private final DefaultListModel<ComicCharacter> selectedCharactersListModel = new DefaultListModel<>();
    private final JList<ComicCharacter> selectedCharactersList = new JList<>(selectedCharactersListModel);
    private final DefaultListModel<ComicCharacter> matchedCharactersListModel = new DefaultListModel<>();
    private final JList<ComicCharacter> matchedCharactersList = new JList<>(matchedCharactersListModel);

    // Services and state
    private final Series currentSeries;
    private final CharacterService characterService = new CharacterService();
    private final CreatorService creatorService = new CreatorService();
    private final IssueService issueService = new IssueService();
    private final Issue existingIssue;
    private final Runnable callback;

    // UI components
    private JLabel seriesNameLabel;
    private JTextField creatorSearchField;
    private JTextField characterSearchField;

    // Data
    private Set<ComicCharacter> allCharacters = new HashSet<>();
    private Set<Creator> allCreators = new HashSet<>();
    private ScheduledFuture<?> creatorSearchTask;
    private ScheduledFuture<?> characterSearchTask;

    /**
     * Creates a form for adding a new issue to a series
     *
     * @param series       The series to add the issue to
     * @param onIssueAdded Callback to run after adding an issue
     */
    public IssueForm(Series series, Runnable onIssueAdded) {
        super("Add New Issue");
        this.currentSeries = series;
        this.existingIssue = null;
        this.callback = onIssueAdded;

        buildUI();
        loadInitialData();
        setupSubmitAction();
    }

    /**
     * Creates a form for editing an existing issue
     *
     * @param existingIssue  The issue to edit
     * @param onIssueUpdated Callback to run after updating the issue
     */
    public IssueForm(Issue existingIssue, Runnable onIssueUpdated) {
        super("Edit Issue");
        this.currentSeries = existingIssue.getSeries();
        this.existingIssue = existingIssue;
        this.callback = onIssueUpdated;

        setEditMode(true);
        buildUI();
        loadInitialData();
        populateFields(existingIssue);
        setupEditAction();
    }

    @Override
    protected void buildUI() {
        int row = 0;

        // Series label
        seriesNameLabel = new JLabel(currentSeries.toString());
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(seriesNameLabel);
        row = addPanel(headerPanel, row);

        // Main issue details
        row = addTextField("Issue #:", issueNumberField, row, true);
        row = addTextArea("Overview:", overviewTextArea, row, 3, false);
        row = addTextField("Release Date:", releaseDateField, row, false);
        row = addTextField("Price (USD):", priceField, row, false);
        row = addTextField("Image URL:", imageUrlField, row, false);
        row = addCheckbox(annualCheckBox, row);

        // Creators section
        JPanel creatorsPanel = createCreatorsPanel();
        row = addPanel(creatorsPanel, row);

        // Characters section
        JPanel charactersPanel = createCharactersPanel();
        row = addPanel(charactersPanel, row);
    }

    /**
     * Creates the panel for managing creators
     */
    private JPanel createCreatorsPanel() {
        JPanel creatorsPanel = createTitledPanel("Creators");
        creatorsPanel.setLayout(new BorderLayout());

        // Setup creator table
        creatorTable.setRowHeight(creatorTable.getRowHeight() * 2);
        JScrollPane creatorTableScrollPane = new JScrollPane(creatorTable);
        creatorTableScrollPane.setPreferredSize(new Dimension(300, 80));
        creatorsPanel.add(creatorTableScrollPane, BorderLayout.CENTER);
        addCreatorRemovalListener();

        // Creator search and selection panel
        JPanel creatorInputPanel = new JPanel(new BorderLayout());

        // Search field
        creatorSearchField = createSearchField("Search for Creators...");
        JPanel creatorSearchPanel = createSearchPanel("Search:", creatorSearchField);
        creatorInputPanel.add(creatorSearchPanel, BorderLayout.NORTH);

        // Role selection
        JPanel creatorRolePanel = new JPanel(new BorderLayout());
        creatorRolePanel.add(new JLabel("Roles:"), BorderLayout.NORTH);
        roleSearchList.setVisibleRowCount(4);
        roleSearchList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        creatorRolePanel.add(new JScrollPane(roleSearchList), BorderLayout.CENTER);

        // Creator results and roles panel
        JPanel matchedAndRolesPanel = new JPanel(new BorderLayout());
        JScrollPane matchedCreatorsScrollPane = new JScrollPane(matchedCreatorsList);
        matchedCreatorsScrollPane.setPreferredSize(new Dimension(200, 80));
        matchedAndRolesPanel.add(matchedCreatorsScrollPane, BorderLayout.WEST);
        matchedAndRolesPanel.add(creatorRolePanel, BorderLayout.CENTER);
        creatorInputPanel.add(matchedAndRolesPanel, BorderLayout.CENTER);

        // Add creator button
        JButton addCreatorByRolesButton = new JButton("Add Creator(s) by Roles");
        addCreatorByRolesButton.addActionListener(e -> addCreatorsByRoles());
        creatorInputPanel.add(addCreatorByRolesButton, BorderLayout.SOUTH);

        creatorsPanel.add(creatorInputPanel, BorderLayout.NORTH);

        // Set up creator search functionality
        setupCreatorSearchListener();

        return creatorsPanel;
    }

    /**
     * Creates the panel for managing characters
     */
    private JPanel createCharactersPanel() {
        JPanel charactersPanel = createTitledPanel("Characters");
        charactersPanel.setLayout(new BorderLayout());

        // Display selected characters
        JScrollPane selectedCharactersScrollPane = new JScrollPane(selectedCharactersList);
        selectedCharactersScrollPane.setPreferredSize(new Dimension(300, 80));
        charactersPanel.add(selectedCharactersScrollPane, BorderLayout.CENTER);
        addCharacterRemovalListener();

        // Character search panel
        characterSearchField = createSearchField("Search for Characters...");

        // Create the search and results panel using the base class method
        JPanel characterInputPanel = createSearchAndResultsPanel("Search Characters", characterSearchField, matchedCharactersList, "Add Character(s)", e -> addSelectedCharacters());

        charactersPanel.add(characterInputPanel, BorderLayout.NORTH);

        // Set up character search functionality
        setupCharacterSearchListener();

        return charactersPanel;
    }

    /**
     * Sets up the submit action for adding a new issue
     */
    private void setupSubmitAction() {
        addSubmitListener(e -> {
            saveOrUpdateIssue();
            resetForm();
        });
    }

    /**
     * Sets up the submit action for editing an existing issue
     */
    private void setupEditAction() {
        removeAllSubmitListeners();
        addSubmitListener(e -> {
            saveOrUpdateIssue();
            SwingUtilities.getWindowAncestor(this).dispose();
        });
    }

    /**
     * Validates and saves or updates an issue
     */
    private void saveOrUpdateIssue() {
        if (!validateForm()) {
            return;
        }

        String issueText = issueNumberField.getText().trim();
        String overview = overviewTextArea.getText().trim();
        String releaseDateText = releaseDateField.getText().trim();
        String priceText = priceField.getText().trim();
        String imageUrl = imageUrlField.getText().trim();
        boolean isAnnual = annualCheckBox.isSelected();

        // Parse issue number
        BigDecimal issueNumber;
        try {
            issueNumber = new BigDecimal(issueText);
        } catch (NumberFormatException ex) {
            showError("Issue number must be a number.");
            issueNumberField.requestFocus();
            return;
        }

        // Parse release date using the helper method from AbstractForm
        LocalDate releaseDate = parseLocalDate(releaseDateText, "Release Date");
        if (releaseDate == null && !releaseDateText.isEmpty()) {
            releaseDateField.requestFocus();
            return;
        }

        // Parse price
        BigDecimal price = null;
        if (!priceText.isEmpty()) {
            try {
                price = new BigDecimal(priceText);
            } catch (NumberFormatException ex) {
                showError("Price must be a number.");
                priceField.requestFocus();
                return;
            }
        }

        List<ComicCharacter> charactersToAdd = getSelectedCharacters();

        saveIssue(issueNumber, overview, releaseDate, price, imageUrl, isAnnual, charactersToAdd);

        // Execute callback
        if (callback != null) {
            callback.run();
        }
    }

    /**
     * Saves an issue (either new or existing) with the provided details
     *
     * @param issueNumber The issue number
     * @param overview    The issue overview text
     * @param releaseDate The release date
     * @param price       The price in USD
     * @param imageUrl    The URL to the issue's image
     * @param isAnnual    Whether this is an annual issue
     * @param characters  The list of characters in the issue
     */
    private void saveIssue(BigDecimal issueNumber, String overview, LocalDate releaseDate, BigDecimal price, String imageUrl, boolean isAnnual, List<ComicCharacter> characters) {

        Issue issue;
        boolean isNew = false;

        if (isEditMode && existingIssue != null) {
            // Update existing issue
            issue = existingIssue;
        } else {
            // Create new issue
            issue = new Issue(currentSeries, issueNumber);
            isNew = true;
        }

        // Set common properties for both new and existing issues
        issue.setIssueNumber(issueNumber);
        issue.setOverview(overview);
        issue.setReleaseDate(releaseDate);
        issue.setPriceUsd(price);
        issue.setImageUrl(imageUrl.isEmpty() ? null : imageUrl);
        issue.setAnnual(isAnnual);

        // Save the issue
        issueService.saveIssue(issue, selectedCreators, characters);

        // Show appropriate success message
        showSuccess(isNew ? "Issue added!" : "Issue updated successfully.");
    }

    /**
     * Gets all selected characters from the list model
     */
    private List<ComicCharacter> getSelectedCharacters() {
        List<ComicCharacter> characters = new ArrayList<>();
        for (int i = 0; i < selectedCharactersListModel.getSize(); i++) {
            characters.add(selectedCharactersListModel.getElementAt(i));
        }
        return characters;
    }

    /**
     * Validates the form fields
     */
    private boolean validateForm() {
        // Check issue number is provided
        if (issueNumberField.getText().trim().isEmpty()) {
            showError("Issue number is required.");
            issueNumberField.requestFocus();
            return false;
        }

        // Validate image URL if provided
        String imageUrl = imageUrlField.getText().trim();
        if (!imageUrl.isEmpty() && !isValidUrl(imageUrl)) {
            showError("Please enter a valid URL for the image.");
            imageUrlField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Sets up listener for creator search field
     */
    private void setupCreatorSearchListener() {
        creatorSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                scheduleCreatorSearch();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                scheduleCreatorSearch();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                // Plain text components don't fire these events
            }
        });
    }

    /**
     * Sets up listener for character search field
     */
    private void setupCharacterSearchListener() {
        characterSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                scheduleCharacterSearch();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                scheduleCharacterSearch();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                // Plain text components don't fire these events
            }
        });
    }

    /**
     * Adds a context menu for removing creators from the table
     */
    private void addCreatorRemovalListener() {
        creatorTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = creatorTable.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < creatorTableModel.getRowCount()) {
                        creatorTable.setRowSelectionInterval(row, row);
                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem removeItem = new JMenuItem("Remove Creator");
                        removeItem.addActionListener(event -> removeSelectedCreator());
                        popupMenu.add(removeItem);
                        popupMenu.show(creatorTable, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    /**
     * Adds a context menu for removing characters from the list
     */
    private void addCharacterRemovalListener() {
        addItemRemovalListener(selectedCharactersList, selectedCharactersListModel, "Remove Character", null);
    }

    /**
     * Removes the selected creator from the table
     */
    private void removeSelectedCreator() {
        int selectedRow = creatorTable.getSelectedRow();
        if (selectedRow != -1) {
            // Get the name of the creator to remove from the selectedCreators list
            String creatorName = (String) creatorTableModel.getValueAt(selectedRow, 0);
            selectedCreators.removeIf(ic -> ic.getCreator().getName().equals(creatorName));
            creatorTableModel.removeRow(selectedRow);
        }
    }

    /**
     * Adds selected creators with their roles
     */
    private void addCreatorsByRoles() {
        List<Creator> creatorsToAdd = matchedCreatorsList.getSelectedValuesList();
        List<Role> selectedRoles = roleSearchList.getSelectedValuesList();

        if (creatorsToAdd.isEmpty() || selectedRoles.isEmpty()) {
            showError("Please select at least one creator and one role.");
            return;
        }

        for (Creator creator : creatorsToAdd) {
            IssueCreator issueCreator = new IssueCreator();
            issueCreator.setCreator(creator);
            issueCreator.setRoles(new HashSet<>(selectedRoles));

            boolean alreadyAdded = selectedCreators.stream().anyMatch(ic -> ic.getCreator().equals(creator));

            if (!alreadyAdded) {
                selectedCreators.add(issueCreator);
                String roleNames = selectedRoles.stream().map(Enum::name).collect(Collectors.joining(", "));
                creatorTableModel.addRow(new Object[]{creator.getName(), roleNames});
            } else {
                showError(creator.getName() + " is already added.");
            }
        }
        matchedCreatorsList.clearSelection();
    }

    /**
     * Adds selected characters to the list
     */
    private void addSelectedCharacters() {
        addSelectedItemsToModel(matchedCharactersList, selectedCharactersListModel, null);
    }

    /**
     * Schedules a search for creators
     */
    private void scheduleCreatorSearch() {
        creatorSearchTask = setupDelayedSearch(creatorSearchTask, creatorSearchField, this::performCreatorSearch, DEFAULT_SEARCH_DELAY);
    }

    /**
     * Performs the actual creator search
     */
    private void performCreatorSearch() {
        String searchText = creatorSearchField.getText().trim().toLowerCase();
        if (searchText.equals("search for creators...")) {
            return;
        }

        SwingUtilities.invokeLater(() -> performFilteredSearch(searchText, allCreators, matchedCreatorsListModel, creator -> creator.getName().toLowerCase().contains(searchText)));
    }

    /**
     * Schedules a search for characters
     */
    private void scheduleCharacterSearch() {
        characterSearchTask = setupDelayedSearch(characterSearchTask, characterSearchField, this::performCharacterSearch, DEFAULT_SEARCH_DELAY);
    }

    /**
     * Performs the actual character search
     */
    private void performCharacterSearch() {
        String searchText = characterSearchField.getText().trim().toLowerCase();
        if (searchText.equals("search for characters...")) {
            return;
        }

        SwingUtilities.invokeLater(() -> performFilteredSearch(searchText, allCharacters, matchedCharactersListModel, character -> character.getName().toLowerCase().contains(searchText) || (character.getAlias() != null && character.getAlias().toLowerCase().contains(searchText))));
    }

    /**
     * Loads initial data for the form
     */
    private void loadInitialData() {
        Executors.newSingleThreadExecutor().submit(() -> {
            allCreators = creatorService.getAllEntities();
            allCharacters = characterService.getAllEntities();
        });
    }

    /**
     * Populates form fields with data from an existing issue
     */
    private void populateFields(Issue issue) {
        // Set basic fields
        seriesNameLabel.setText(issue.getSeries().getTitle());
        issueNumberField.setText(issue.getIssueNumber() != null ? issue.getIssueNumber().toString() : "");
        overviewTextArea.setText(issue.getOverview() != null ? issue.getOverview() : "");
        releaseDateField.setText(issue.getReleaseDate() != null ? issue.getReleaseDate().toString() : "");
        priceField.setText(issue.getPriceUsd() != null ? issue.getPriceUsd().toString() : "");
        imageUrlField.setText(issue.getImageUrl() != null ? issue.getImageUrl() : "");
        annualCheckBox.setSelected(issue.getAnnual() != null ? issue.getAnnual() : false);

        // Fill creators
        populateCreators(issue);

        // Fill characters
        populateCharacters(issue);
    }

    /**
     * Populates creator data from an existing issue
     */
    private void populateCreators(Issue issue) {
        for (IssueCreator issueCreator : issue.getIssueCreators()) {
            boolean alreadyAdded = selectedCreators.stream().anyMatch(ic -> ic.getCreator().equals(issueCreator.getCreator()));

            if (!alreadyAdded) {
                selectedCreators.add(issueCreator);
                String roleNames = issueCreator.getRoles().stream().map(Enum::name).collect(Collectors.joining(", "));
                creatorTableModel.addRow(new Object[]{issueCreator.getCreator().getName(), roleNames});
            }
        }
    }

    /**
     * Populates character data from an existing issue
     */
    private void populateCharacters(Issue issue) {
        for (ComicCharacter character : issue.getCharacters()) {
            if (!listModelContains(selectedCharactersListModel, character)) {
                selectedCharactersListModel.addElement(character);
            }
        }
    }

    @Override
    protected void resetForm() {
        // Reset basic fields
        issueNumberField.setText("");
        overviewTextArea.setText("");
        releaseDateField.setText("");
        priceField.setText("");
        imageUrlField.setText("");
        annualCheckBox.setSelected(false);

        // Reset creators
        creatorTableModel.setRowCount(0);
        selectedCreators.clear();
        matchedCreatorsListModel.clear();
        roleSearchList.clearSelection();
        creatorSearchField.setText("Search for Creators...");
        creatorSearchField.setForeground(Color.GRAY);

        // Reset characters
        selectedCharactersListModel.clear();
        matchedCharactersListModel.clear();
        matchedCharactersList.clearSelection();
        characterSearchField.setText("Search for Characters...");
        characterSearchField.setForeground(Color.GRAY);
    }
}
