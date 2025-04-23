package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.*;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.service.IssueService;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
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

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class IssueForm extends AbstractForm {

    private final JTextField issueNumberField = new JTextField(10);
    private final JTextArea overviewTextArea = new JTextArea(3, 20);
    private final JTextField releaseDateField = new JTextField(10);
    private final JTextField priceField = new JTextField(10);
    private final JCheckBox annualCheckBox = new JCheckBox("Annual Issue");

    private final DefaultTableModel creatorTableModel = new DefaultTableModel(new Object[]{"Name", "Role(s)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Make table non-editable
        }
    };
    private final JTable creatorTable = new JTable(creatorTableModel);
    private final JTextField creatorSearchField = new JTextField(15);
    private final JList<Role> roleSearchList = new JList<>(Role.values());
    private final DefaultListModel<Creator> matchedCreatorsListModel = new DefaultListModel<>();
    private final JList<Creator> matchedCreatorsList = new JList<>(matchedCreatorsListModel);
    private final List<IssueCreator> selectedCreators = new ArrayList<>();

    private final DefaultListModel<ComicCharacter> selectedCharactersListModel = new DefaultListModel<>();
    private final JList<ComicCharacter> selectedCharactersList = new JList<>(selectedCharactersListModel);
    private final JTextField characterSearchField = new JTextField(15);
    private final DefaultListModel<ComicCharacter> matchedCharactersListModel = new DefaultListModel<>();
    private final JList<ComicCharacter> matchedCharactersList = new JList<>(matchedCharactersListModel);

    private final Series currentSeries;
    private final CharacterService characterService = new CharacterService();
    private final CreatorService creatorService = new CreatorService();
    private final IssueService issueService = new IssueService();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final long SEARCH_DELAY = 300; // milliseconds
    private final Issue existingIssue; // To hold the issue being edited
    private Set<ComicCharacter> allCharacters = new HashSet<>();
    private Set<Creator> allCreators = new HashSet<>();
    private ScheduledFuture<?> creatorSearchTask;
    private ScheduledFuture<?> characterSearchTask;

    public IssueForm(Series series, Runnable onIssueAdded, JDialog parentDialog) {
        this(series, onIssueAdded, parentDialog, null);
    }

    public IssueForm(Issue existingIssue, Runnable onIssueUpdated, JDialog parentDialog) {
        this(existingIssue.getSeries(), onIssueUpdated, parentDialog, existingIssue);
        setSubmitButtonText("Save Changes");

        // Clear existing listeners and add the edit logic
        removeAllSubmitListeners();
        addSubmitListener(_ -> saveOrUpdateIssue(onIssueUpdated, parentDialog));
    }

    private IssueForm(Series series, Runnable callback, JDialog parentDialog, Issue existingIssue) {
        super("Add New Issue");
        this.currentSeries = series;
        this.existingIssue = existingIssue; // Store the existing issue if available
        JLabel seriesNameLabel = new JLabel();
        seriesNameLabel.setText(series.toString());
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel annualPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        annualPanel.add(annualCheckBox);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Main Issue Details
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(seriesNameLabel);
        contentPanel.add(headerPanel);

        JPanel issueInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        issueInfoPanel.add(new JLabel("Issue #:"), gbc);
        gbc.gridx = 1;
        issueInfoPanel.add(issueNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        issueInfoPanel.add(new JLabel("Overview:"), gbc);
        gbc.gridx = 1;
        overviewTextArea.setLineWrap(true);
        overviewTextArea.setWrapStyleWord(true);
        JScrollPane overviewScrollPane = new JScrollPane(overviewTextArea);
        overviewScrollPane.setPreferredSize(new Dimension(300, 60));
        issueInfoPanel.add(overviewScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        issueInfoPanel.add(new JLabel("Release Date:"), gbc);
        gbc.gridx = 1;
        issueInfoPanel.add(releaseDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        issueInfoPanel.add(new JLabel("Price (USD):"), gbc);
        gbc.gridx = 1;
        issueInfoPanel.add(priceField, gbc);

        contentPanel.add(issueInfoPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Annual Checkbox Panel
        contentPanel.add(annualPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Creators Section
        JPanel creatorsPanel = new JPanel(new BorderLayout());
        creatorsPanel.setBorder(BorderFactory.createTitledBorder("Creators"));
        creatorsPanel.add(new JScrollPane(creatorTable), BorderLayout.CENTER);
        creatorTable.setRowHeight(creatorTable.getRowHeight() * 2);
        creatorTable.setPreferredScrollableViewportSize(new Dimension(300, 80));
        addCreatorRemovalListener();

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

        JPanel creatorRolePanel = new JPanel(new BorderLayout());
        creatorRolePanel.add(new JLabel("Roles:"), BorderLayout.NORTH);
        roleSearchList.setVisibleRowCount(4);
        roleSearchList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        creatorRolePanel.add(new JScrollPane(roleSearchList), BorderLayout.CENTER);

        JPanel matchedAndRolesPanel = new JPanel(new BorderLayout());
        JScrollPane matchedCreatorsScrollPane = new JScrollPane(matchedCreatorsList);
        matchedCreatorsScrollPane.setPreferredSize(new Dimension(200, 80));
        matchedAndRolesPanel.add(matchedCreatorsScrollPane, BorderLayout.WEST);
        matchedAndRolesPanel.add(creatorRolePanel, BorderLayout.CENTER);

        JPanel creatorInputPanel = new JPanel(new BorderLayout());
        creatorInputPanel.add(creatorSearchPanel, BorderLayout.NORTH);
        creatorInputPanel.add(matchedAndRolesPanel, BorderLayout.CENTER);
        JButton addCreatorByRolesButton = new JButton("Add Creator(s) by Roles");
        creatorInputPanel.add(addCreatorByRolesButton, BorderLayout.SOUTH);
        addCreatorByRolesButton.addActionListener(e -> addCreatorsByRoles());

        creatorsPanel.add(creatorInputPanel, BorderLayout.NORTH);

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
            }
        });

        contentPanel.add(creatorsPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Characters Section
        JPanel charactersPanel = new JPanel(new BorderLayout());
        charactersPanel.setBorder(BorderFactory.createTitledBorder("Characters"));

        // Display selected characters
        JScrollPane selectedCharactersScrollPane = new JScrollPane(selectedCharactersList);
        selectedCharactersScrollPane.setPreferredSize(new Dimension(300, 80));
        charactersPanel.add(selectedCharactersScrollPane, BorderLayout.CENTER);
        addCharacterRemovalListener();

        JPanel characterInputPanel = new JPanel(new BorderLayout());
        JPanel characterSearchPanel = new JPanel(new BorderLayout()); // Use BorderLayout for searchPanel
        characterSearchField.setText("Search for Characters...");
        characterSearchField.setForeground(Color.GRAY);
        characterSearchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (characterSearchField.getText().equals("Search for Characters...")) {
                    characterSearchField.setText("");
                    characterSearchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (characterSearchField.getText().isEmpty()) {
                    characterSearchField.setForeground(Color.GRAY);
                    characterSearchField.setText("Search for Characters...");
                }
            }
        });
        characterSearchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        characterSearchPanel.add(characterSearchField, BorderLayout.CENTER); // Search field fills center
        characterInputPanel.add(characterSearchPanel, BorderLayout.NORTH);
        JScrollPane matchedCharactersScrollPane = new JScrollPane(matchedCharactersList);
        matchedCharactersScrollPane.setPreferredSize(new Dimension(300, 80));
        characterInputPanel.add(matchedCharactersScrollPane, BorderLayout.CENTER);
        JButton addCharactersButton = new JButton("Add Character(s)");
        characterInputPanel.add(addCharactersButton, BorderLayout.SOUTH); // Button at the bottom
        addCharactersButton.addActionListener(e -> addSelectedCharacters());
        charactersPanel.add(characterInputPanel, BorderLayout.NORTH);

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
            }
        });

        contentPanel.add(charactersPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);

        addSubmitListener(e -> saveOrUpdateIssue(callback, parentDialog));

        // Load data in the background
        loadInitialData();

        if (existingIssue != null) {
            // Fill in the existing data
            seriesNameLabel.setText(existingIssue.getSeries().getTitle());
            issueNumberField.setText(existingIssue.getIssueNumber() != null ? existingIssue.getIssueNumber().toString() : "");
            overviewTextArea.setText(existingIssue.getOverview() != null ? existingIssue.getOverview() : "");
            releaseDateField.setText(existingIssue.getReleaseDate() != null ? existingIssue.getReleaseDate().toString() : "");
            priceField.setText(existingIssue.getPriceUsd() != null ? existingIssue.getPriceUsd().toString() : "");
            annualCheckBox.setSelected(existingIssue.getAnnual() != null ? existingIssue.getAnnual() : false);

            // Fill creators
            for (IssueCreator issueCreator : existingIssue.getIssueCreators()) {
                // Simulate adding creators by roles
                Set<Role> roles = issueCreator.getRoles();
                Creator creator = issueCreator.getCreator();

                boolean alreadyAdded = selectedCreators.stream()
                        .anyMatch(ic -> ic.getCreator().equals(creator));

                if (!alreadyAdded) {
                    selectedCreators.add(issueCreator);
                    String roleNames = roles.stream().map(Enum::name).collect(Collectors.joining(", "));
                    creatorTableModel.addRow(new Object[]{creator.getName(), roleNames});
                }
            }

            // Fill characters
            for (ComicCharacter character : existingIssue.getCharacters()) {
                if (!selectedCharactersListModel.contains(character)) {
                    selectedCharactersListModel.addElement(character);
                }
            }
        }
    }

    private void saveOrUpdateIssue(Runnable callback, JDialog parentDialog) {
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
            return;
        }

        LocalDate releaseDate = null;
        if (!releaseDateText.isEmpty()) {
            try {
                releaseDate = LocalDate.parse(releaseDateText, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException ex) {
                showError("Invalid date format. Please use <:code-block language=text>YYYY-MM-DD</:code-block>.");
                return;
            }
        }

        BigDecimal price = null;
        if (!priceText.isEmpty()) {
            try {
                price = new BigDecimal(priceText);
            } catch (NumberFormatException ex) {
                showError("Price must be a number.");
                return;
            }
        }

        List<ComicCharacter> charactersToAdd = new ArrayList<>();
        for (int i = 0; i < selectedCharactersListModel.getSize(); i++) {
            charactersToAdd.add(selectedCharactersListModel.getElementAt(i));
        }
        if (existingIssue != null) { // Update existing issue
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
            existingIssue.getIssueCreators().clear(); // Clear existing creators
            for (IssueCreator ic : selectedCreators) {
                ic.setIssue(existingIssue);
            }
            existingIssue.getIssueCreators().addAll(selectedCreators);

            // Update characters
            existingIssue.getCharacters().clear(); // Clear existing characters
            for (ComicCharacter character : charactersToAdd) {
                existingIssue.addCharacter(character); // Use the addCharacter method to maintain bidirectional relationship
            }
            issueService.updateIssue(existingIssue);
            showSuccess("Issue updated successfully.");
        } else { // Save new issue
            issueService.addIssue(currentSeries, issueNumber, overview, releaseDate, price, isAnnual, selectedCreators, charactersToAdd);
            showSuccess("Issue added!");
            resetForm(); // Only reset if a new issue was added
        }

        SwingUtilities.getWindowAncestor(this).dispose();
        if (callback != null) {
            callback.run();
        }
        if (parentDialog != null) {
            parentDialog.dispose();
        }
    }

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
                        removeItem.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent event) {
                                removeSelectedCreator();
                            }
                        });
                        popupMenu.add(removeItem);
                        popupMenu.show(creatorTable, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private void removeSelectedCreator() {
        int selectedRow = creatorTable.getSelectedRow();
        if (selectedRow != -1) {
            // Get the name of the creator to remove from the selectedCreators list
            String creatorName = (String) creatorTableModel.getValueAt(selectedRow, 0);
            selectedCreators.removeIf(ic -> ic.getCreator().getName().equals(creatorName));
            creatorTableModel.removeRow(selectedRow);
        }
    }

    private void addCharacterRemovalListener() {
        selectedCharactersList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = selectedCharactersList.locationToIndex(e.getPoint());
                    if (index >= 0 && index < selectedCharactersListModel.getSize()) {
                        selectedCharactersList.setSelectedIndex(index);
                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem removeItem = new JMenuItem("Remove Character");
                        removeItem.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent event) {
                                removeSelectedCharacter();
                            }
                        });
                        popupMenu.add(removeItem);
                        popupMenu.show(selectedCharactersList, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private void removeSelectedCharacter() {
        int selectedIndex = selectedCharactersList.getSelectedIndex();
        if (selectedIndex != -1) {
            selectedCharactersListModel.remove(selectedIndex);
        }
    }

    private void loadInitialData() {
        Executors.newSingleThreadExecutor().submit(() -> {
            allCreators = creatorService.getAllCreators();
            allCharacters = characterService.getAllCharacters();
        });
    }

    private void scheduleCreatorSearch() {
        if (creatorSearchTask != null) {
            creatorSearchTask.cancel(true);
        }
        creatorSearchTask = scheduler.schedule(() -> {
            String search = creatorSearchField.getText().trim().toLowerCase();
            SwingUtilities.invokeLater(() -> {
                matchedCreatorsListModel.clear();
                allCreators.stream()
                        .filter(creator -> creator.getName().toLowerCase().contains(search))
                        .forEach(matchedCreatorsListModel::addElement);
            });
        }, SEARCH_DELAY, TimeUnit.MILLISECONDS);
    }

    private void scheduleCharacterSearch() {
        if (characterSearchTask != null) {
            characterSearchTask.cancel(true);
        }
        characterSearchTask = scheduler.schedule(() -> {
            String search = characterSearchField.getText().trim().toLowerCase();
            SwingUtilities.invokeLater(() -> {
                matchedCharactersListModel.clear();
                allCharacters.stream()
                        .filter(character -> character.getName().toLowerCase().contains(search) ||
                                (character.getAlias() != null && character.getAlias().toLowerCase().contains(search)))
                        .forEach(matchedCharactersListModel::addElement);
            });
        }, SEARCH_DELAY, TimeUnit.MILLISECONDS);
    }

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

            boolean alreadyAdded = selectedCreators.stream()
                    .anyMatch(ic -> ic.getCreator().equals(creator));

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

    private void addSelectedCharacters() {
        List<ComicCharacter> charactersToAdd = matchedCharactersList.getSelectedValuesList();
        for (ComicCharacter character : charactersToAdd) {
            if (!selectedCharactersListModel.contains(character)) {
                selectedCharactersListModel.addElement(character);
            }
        }
        matchedCharactersList.clearSelection();
    }

    private void resetForm() {
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
        characterSearchField.setText("");
        creatorSearchField.setText("");
        roleSearchList.clearSelection();
    }
}
