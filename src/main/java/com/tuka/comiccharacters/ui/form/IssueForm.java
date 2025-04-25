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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class IssueForm extends AbstractForm {

    // Form fields
    private final JTextField issueNumberField = new JTextField(10);
    private final JTextArea overviewTextArea = new JTextArea(3, 20);
    private final JTextField releaseDateField = new JTextField(10);
    private final JTextField priceField = new JTextField(10);
    private final JCheckBox annualCheckBox = new JCheckBox("Annual Issue");
    // Creators section
    private final DefaultTableModel creatorTableModel = new DefaultTableModel(new Object[]{"Name", "Role(s)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Make table non-editable
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
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final long SEARCH_DELAY = 300; // milliseconds
    private final Issue existingIssue; // To hold the issue being edited
    // Callback for after form submission
    private final Runnable callback;
    private final JDialog parentDialog;
    private JLabel seriesNameLabel;
    private JTextField creatorSearchField;
    private JTextField characterSearchField;
    private Set<ComicCharacter> allCharacters = new HashSet<>();
    private Set<Creator> allCreators = new HashSet<>();
    private ScheduledFuture<?> creatorSearchTask;
    private ScheduledFuture<?> characterSearchTask;

    /**
     * Creates a form for adding a new issue to a series
     *
     * @param series       The series to add the issue to
     * @param onIssueAdded Callback to run after adding an issue
     * @param parentDialog The parent dialogue to close after submission
     */
    public IssueForm(Series series, Runnable onIssueAdded, JDialog parentDialog) {
        super("Add New Issue");
        this.currentSeries = series;
        this.existingIssue = null;
        this.callback = onIssueAdded;
        this.parentDialog = parentDialog;

        buildUI();
        loadInitialData();
        setupSubmitAction();
    }

    /**
     * Creates a form for editing an existing issue
     *
     * @param existingIssue  The issue to edit
     * @param onIssueUpdated Callback to run after updating the issue
     * @param parentDialog   The parent dialogue to close after submission
     */
    public IssueForm(Issue existingIssue, Runnable onIssueUpdated, JDialog parentDialog) {
        super("Edit Issue");
        this.currentSeries = existingIssue.getSeries();
        this.existingIssue = existingIssue;
        this.callback = onIssueUpdated;
        this.parentDialog = parentDialog;

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

        // Annual checkbox
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
     *
     * @return The creators panel
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
        JPanel creatorSearchPanel = new JPanel(new BorderLayout());
        creatorSearchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        creatorSearchPanel.add(creatorSearchField, BorderLayout.CENTER);
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
     *
     * @return The characters panel
     */
    private JPanel createCharactersPanel() {
        JPanel charactersPanel = createTitledPanel("Characters");
        charactersPanel.setLayout(new BorderLayout());

        // Display selected characters
        JScrollPane selectedCharactersScrollPane = new JScrollPane(selectedCharactersList);
        selectedCharactersScrollPane.setPreferredSize(new Dimension(300, 80));
        charactersPanel.add(selectedCharactersScrollPane, BorderLayout.CENTER);
        addCharacterRemovalListener();

        // Character search and selection panel
        JPanel characterInputPanel = new JPanel(new BorderLayout());

        // Search field
        characterSearchField = createSearchField("Search for Characters...");
        JPanel characterSearchPanel = new JPanel(new BorderLayout());
        characterSearchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        characterSearchPanel.add(characterSearchField, BorderLayout.CENTER);
        characterInputPanel.add(characterSearchPanel, BorderLayout.NORTH);

        // Character results
        JScrollPane matchedCharactersScrollPane = new JScrollPane(matchedCharactersList);
        matchedCharactersScrollPane.setPreferredSize(new Dimension(300, 80));
        characterInputPanel.add(matchedCharactersScrollPane, BorderLayout.CENTER);

        // Add character button
        JButton addCharactersButton = new JButton("Add Character(s)");
        addCharactersButton.addActionListener(e -> addSelectedCharacters());
        characterInputPanel.add(addCharactersButton, BorderLayout.SOUTH);

        charactersPanel.add(characterInputPanel, BorderLayout.NORTH);

        // Set up character search functionality
        setupCharacterSearchListener();

        return charactersPanel;
    }

    /**
     * Sets up the submit action for adding a new issue
     */
    private void setupSubmitAction() {
        addSubmitListener(e -> saveOrUpdateIssue());
    }

    /**
     * Sets up the submit action for editing an existing issue
     */
    private void setupEditAction() {
        removeAllSubmitListeners();
        addSubmitListener(e -> saveOrUpdateIssue());
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
        boolean isAnnual = annualCheckBox.isSelected();

        BigDecimal issueNumber;
        try {
            issueNumber = new BigDecimal(issueText);
        } catch (NumberFormatException ex) {
            showError("Issue number must be a number.");
            issueNumberField.requestFocus();
            return;
        }

        LocalDate releaseDate = null;
        if (!releaseDateText.isEmpty()) {
            try {
                releaseDate = LocalDate.parse(releaseDateText, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException ex) {
                showError("Invalid date format. Please use YYYY-MM-DD.");
                releaseDateField.requestFocus();
                return;
            }
        }

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

        if (isEditMode && existingIssue != null) {
            updateIssue(issueNumber, overview, releaseDate, price, isAnnual, charactersToAdd);
        } else {
            addIssue(issueNumber, overview, releaseDate, price, isAnnual, charactersToAdd);
        }

        // Close window and execute callback
        SwingUtilities.getWindowAncestor(this).dispose();
        if (callback != null) {
            callback.run();
        }
        if (parentDialog != null) {
            parentDialog.dispose();
        }
    }

    /**
     * Updates an existing issue with new values
     */
    private void updateIssue(BigDecimal issueNumber, String overview, LocalDate releaseDate, BigDecimal price, boolean isAnnual, List<ComicCharacter> characters) {
        existingIssue.setIssueNumber(issueNumber);
        existingIssue.setOverview(overview);
        if (releaseDate != null) {
            existingIssue.setReleaseDate(releaseDate);
        }
        if (price != null) {
            existingIssue.setPriceUsd(price);
        }
        existingIssue.setAnnual(isAnnual);

        // Update creators
        existingIssue.getIssueCreators().clear();
        for (IssueCreator ic : selectedCreators) {
            ic.setIssue(existingIssue);
        }
        existingIssue.getIssueCreators().addAll(selectedCreators);

        // Update characters
        existingIssue.getCharacters().clear();
        for (ComicCharacter character : characters) {
            existingIssue.addCharacter(character);
        }

        issueService.updateIssue(existingIssue);
        showSuccess("Issue updated successfully.");
    }

    /**
     * Adds a new issue to the database
     */
    private void addIssue(BigDecimal issueNumber, String overview, LocalDate releaseDate, BigDecimal price, boolean isAnnual, List<ComicCharacter> characters) {
        issueService.addIssue(currentSeries, issueNumber, overview, releaseDate, price, isAnnual, selectedCreators, characters);
        showSuccess("Issue added!");
    }

    /**
     * Gets all selected characters from the list model
     *
     * @return List of selected characters
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
     *
     * @return Whether the form is valid
     */
    private boolean validateForm() {
        // Check issue number is provided
        if (issueNumberField.getText().trim().isEmpty()) {
            showError("Issue number is required.");
            issueNumberField.requestFocus();
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
        List<ComicCharacter> charactersToAdd = matchedCharactersList.getSelectedValuesList();
        for (ComicCharacter character : charactersToAdd) {
            if (!selectedCharactersListModel.contains(character)) {
                selectedCharactersListModel.addElement(character);
            }
        }
        matchedCharactersList.clearSelection();
    }

    /**
     * Schedules a search for creators
     */
    private void scheduleCreatorSearch() {
        if (creatorSearchTask != null) {
            creatorSearchTask.cancel(true);
        }
        creatorSearchTask = scheduler.schedule(() -> {
            String search = creatorSearchField.getText().trim().toLowerCase();
            if (search.equals("search for creators...")) {
                return;
            }
            SwingUtilities.invokeLater(() -> {
                matchedCreatorsListModel.clear();
                allCreators.stream().filter(creator -> creator.getName().toLowerCase().contains(search)).forEach(matchedCreatorsListModel::addElement);
            });
        }, SEARCH_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Schedules a search for characters
     */
    private void scheduleCharacterSearch() {
        if (characterSearchTask != null) {
            characterSearchTask.cancel(true);
        }
        characterSearchTask = scheduler.schedule(() -> {
            String search = characterSearchField.getText().trim().toLowerCase();
            if (search.equals("search for characters...")) {
                return;
            }
            SwingUtilities.invokeLater(() -> {
                matchedCharactersListModel.clear();
                allCharacters.stream().filter(character -> character.getName().toLowerCase().contains(search) || (character.getAlias() != null && character.getAlias().toLowerCase().contains(search))).forEach(matchedCharactersListModel::addElement);
            });
        }, SEARCH_DELAY, TimeUnit.MILLISECONDS);
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
     *
     * @param issue The issue to load data from
     */
    private void populateFields(Issue issue) {
        seriesNameLabel.setText(issue.getSeries().getTitle());
        issueNumberField.setText(issue.getIssueNumber() != null ? issue.getIssueNumber().toString() : "");
        overviewTextArea.setText(issue.getOverview() != null ? issue.getOverview() : "");
        releaseDateField.setText(issue.getReleaseDate() != null ? issue.getReleaseDate().toString() : "");
        priceField.setText(issue.getPriceUsd() != null ? issue.getPriceUsd().toString() : "");
        annualCheckBox.setSelected(issue.getAnnual() != null ? issue.getAnnual() : false);

        // Fill creators
        for (IssueCreator issueCreator : issue.getIssueCreators()) {
            Set<Role> roles = issueCreator.getRoles();
            Creator creator = issueCreator.getCreator();

            boolean alreadyAdded = selectedCreators.stream().anyMatch(ic -> ic.getCreator().equals(creator));

            if (!alreadyAdded) {
                selectedCreators.add(issueCreator);
                String roleNames = roles.stream().map(Enum::name).collect(Collectors.joining(", "));
                creatorTableModel.addRow(new Object[]{creator.getName(), roleNames});
            }
        }

        // Fill characters
        for (ComicCharacter character : issue.getCharacters()) {
            if (!selectedCharactersListModel.contains(character)) {
                selectedCharactersListModel.addElement(character);
            }
        }
    }

    @Override
    protected void resetForm() {
        issueNumberField.setText("");
        overviewTextArea.setText("");
        releaseDateField.setText("");
        priceField.setText("");
        annualCheckBox.setSelected(false);
        creatorTableModel.setRowCount(0);
        selectedCreators.clear();
        selectedCharactersListModel.clear();
        matchedCreatorsListModel.clear();
        matchedCharactersList.clearSelection();
        creatorSearchField.setText("Search for Creators...");
        creatorSearchField.setForeground(Color.GRAY);
        characterSearchField.setText("Search for Characters...");
        characterSearchField.setForeground(Color.GRAY);
        roleSearchList.clearSelection();
    }
}
