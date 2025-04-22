package com.tuka.comiccharacters.ui.form;

import com.tuka.comiccharacters.model.*;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.service.IssueService;
import com.tuka.comiccharacters.service.SeriesService;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

    private final JLabel seriesNameLabel = new JLabel();
    private final JTextField issueNumberField = new JTextField(10);
    private final JTextArea overviewTextArea = new JTextArea(3, 20);
    private final JScrollPane overviewScrollPane = new JScrollPane(overviewTextArea);
    private final JTextField releaseDateField = new JTextField(10);
    private final JTextField priceField = new JTextField(10);

    // Creators Section
    private final JPanel creatorsPanel = new JPanel(new BorderLayout());
    private final DefaultTableModel creatorTableModel = new DefaultTableModel(new Object[]{"Name", "Role(s)"}, 0);
    private final JTable creatorTable = new JTable(creatorTableModel);
    private final JTextField creatorSearchField = new JTextField(15);
    private final JList<Role> roleSearchList = new JList<>(Role.values());
    private final JScrollPane roleSearchScrollPane = new JScrollPane(roleSearchList);
    private final DefaultListModel<Creator> matchedCreatorsListModel = new DefaultListModel<>();
    private final JList<Creator> matchedCreatorsList = new JList<>(matchedCreatorsListModel);
    private final JScrollPane matchedCreatorsScrollPane = new JScrollPane(matchedCreatorsList);
    private final List<IssueCreator> selectedCreators = new ArrayList<>();

    // Characters Section
    private final JPanel charactersPanel = new JPanel(new BorderLayout());
    private final DefaultListModel<ComicCharacter> selectedCharactersListModel = new DefaultListModel<>();
    private final JList<ComicCharacter> selectedCharactersList = new JList<>(selectedCharactersListModel);
    private final JScrollPane selectedCharactersScrollPane = new JScrollPane(selectedCharactersList);
    private final JTextField characterSearchField = new JTextField(15);
    private final DefaultListModel<ComicCharacter> matchedCharactersListModel = new DefaultListModel<>();
    private final JList<ComicCharacter> matchedCharactersList = new JList<>(matchedCharactersListModel);
    private final JScrollPane matchedCharactersScrollPane = new JScrollPane(matchedCharactersList);

    private final Series currentSeries;
    private final SeriesService seriesService = new SeriesService();
    private final CharacterService characterService = new CharacterService();
    private final CreatorService creatorService = new CreatorService();
    private final IssueService issueService = new IssueService();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final long SEARCH_DELAY = 300; // milliseconds
    private Set<ComicCharacter> allCharacters = new HashSet<>();
    private Set<Creator> allCreators = new HashSet<>();
    private ScheduledFuture<?> creatorSearchTask;
    private ScheduledFuture<?> characterSearchTask;

    public IssueForm(Series series, Runnable onIssueAdded, JDialog parentDialog) {
        super("Add New Issue");
        this.currentSeries = series;
        seriesNameLabel.setText(series.toString());
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        // Creators Section
        creatorsPanel.setBorder(BorderFactory.createTitledBorder("Creators"));
        creatorsPanel.add(new JScrollPane(creatorTable), BorderLayout.CENTER);
        creatorTable.setRowHeight(creatorTable.getRowHeight() * 2);
        creatorTable.setPreferredScrollableViewportSize(new Dimension(300, 80));

        JPanel creatorSearchPanel = new JPanel(new BorderLayout());
        creatorSearchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        creatorSearchPanel.add(creatorSearchField, BorderLayout.CENTER);

        JPanel creatorRolePanel = new JPanel(new BorderLayout());
        creatorRolePanel.add(new JLabel("Roles:"), BorderLayout.NORTH);
        roleSearchList.setVisibleRowCount(4);
        roleSearchList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        creatorRolePanel.add(new JScrollPane(roleSearchList), BorderLayout.CENTER);

        JPanel matchedAndRolesPanel = new JPanel(new BorderLayout());
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
        charactersPanel.setBorder(BorderFactory.createTitledBorder("Characters"));
        charactersPanel.add(new JScrollPane(selectedCharactersList), BorderLayout.CENTER);
        selectedCharactersScrollPane.setPreferredSize(new Dimension(300, 80));

        JPanel characterInputPanel = new JPanel(new BorderLayout());
        JPanel characterSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        characterSearchPanel.add(new JLabel("Search:"));
        characterSearchPanel.add(characterSearchField);
        characterInputPanel.add(characterSearchPanel, BorderLayout.NORTH);
        matchedCharactersScrollPane.setPreferredSize(new Dimension(300, 80));
        characterInputPanel.add(matchedCharactersScrollPane, BorderLayout.CENTER);
        JButton addCharactersButton = new JButton("Add Character(s)");
        characterInputPanel.add(addCharactersButton, BorderLayout.SOUTH);
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

        addSubmitListener(e -> saveIssue(onIssueAdded, parentDialog));

        // Load data in the background
        loadInitialData();
    }

    private void loadInitialData() {
        Executors.newSingleThreadExecutor().submit(() -> {
            allCreators = creatorService.getAllCreators();
            allCharacters = characterService.getAllCharacters();
            SwingUtilities.invokeLater(() -> {
                 matchedCreatorsListModel.addAll(allCreators);
                 matchedCharactersListModel.addAll(allCharacters);
            });
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

    private void saveIssue(Runnable onIssueAdded, JDialog parentDialog) {
        String issueText = issueNumberField.getText().trim();
        String overview = overviewTextArea.getText().trim();
        String releaseDateText = releaseDateField.getText().trim();
        String priceText = priceField.getText().trim();

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
                showError("Invalid date format. Please use<ctrl3348>-MM-DD.");
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

        issueService.addIssue(currentSeries, issueNumber, overview, releaseDate, price, selectedCreators, charactersToAdd);
        showSuccess("Issue added!");
        resetForm();
        if (onIssueAdded != null) {
            onIssueAdded.run();
        }
        if (parentDialog != null) {
            parentDialog.dispose();
        }
    }

    private void resetForm() {
        issueNumberField.setText("");
        overviewTextArea.setText("");
        releaseDateField.setText("");
        priceField.setText("");
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