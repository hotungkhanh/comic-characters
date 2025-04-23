package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.IssueCreator;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.ui.MainApp;
import com.tuka.comiccharacters.ui.form.CreatorForm;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CreatorDetails extends AbstractDetails<Creator> {

    public CreatorDetails(Component parent, Creator creator, Runnable refreshCallback) {
        super(parent, creator, refreshCallback);
    }

    public void showDetailsDialog() {
        super.showDetailsDialog(600, 700);
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        row = addLabelValue(infoPanel, "Name:", entity.getName(), row);

        // Overview
        if (entity.getOverview() != null && !entity.getOverview().isBlank()) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            infoPanel.add(new JLabel("Overview:"), gbc);

            gbc.gridx = 1;
            JTextArea overviewArea = new JTextArea(entity.getOverview());
            overviewArea.setLineWrap(true);
            overviewArea.setWrapStyleWord(true);
            overviewArea.setEditable(false);
            overviewArea.setBackground(infoPanel.getBackground());
            overviewArea.setBorder(null);
            overviewArea.setFont(UIManager.getFont("Label.font"));
            overviewArea.setRows(5);
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            infoPanel.add(new JScrollPane(overviewArea), gbc);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 0.0;
            row++;
        }

        // Credited Characters Panel
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;

        JPanel charactersPanel = new JPanel(new BorderLayout());
        charactersPanel.setBorder(BorderFactory.createTitledBorder("Credited Characters"));

        List<ComicCharacter> sortedCharacters = entity.getCreditedCharacters().stream()
                .sorted(Comparator.comparing(ComicCharacter::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        DefaultListModel<String> characterListModel = new DefaultListModel<>();
        for (ComicCharacter character : sortedCharacters) {
            characterListModel.addElement(character.toString());
        }

        JList<String> characterList = new JList<>(characterListModel);
        characterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        charactersPanel.add(new JScrollPane(characterList), BorderLayout.CENTER);
        charactersPanel.setPreferredSize(new Dimension(400, 100));
        infoPanel.add(charactersPanel, gbc);

        // Credited Issues Panel
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;

        JPanel issuesPanel = new JPanel(new BorderLayout());
        issuesPanel.setBorder(BorderFactory.createTitledBorder("Credited Issues"));

        Set<Issue> creditedIssues = entity.getIssueCreators().stream()
                .map(IssueCreator::getIssue)
                .sorted(Comparator.comparing(Issue::toString, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toSet());

        DefaultListModel<Issue> issueListModel = new DefaultListModel<>();
        for (Issue issue : creditedIssues) {
            issueListModel.addElement(issue);
        }

        JList<Issue> issueList = new JList<>(issueListModel);
        issueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        issuesPanel.add(new JScrollPane(issueList), BorderLayout.CENTER);
        issuesPanel.setPreferredSize(new Dimension(400, 150));
        infoPanel.add(issuesPanel, gbc);

        mainPanel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        return mainPanel;
    }

    private int addLabelValue(JPanel panel, String label, String value, int row) {
        if (value == null || value.isBlank()) return row;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(new JLabel(value), gbc);
        gbc.weightx = 0.0;

        return row + 1;
    }

    @Override
    protected String getTitle() {
        return "Creator Details";
    }

    @Override
    protected void showEditDialog() {
        JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Edit Creator", true);
        CreatorForm editPanel = new CreatorForm(entity);
        editDialog.setContentPane(editPanel);
        editDialog.pack();
        editDialog.setLocationRelativeTo(parent);
        editDialog.setVisible(true);
        refreshCallback.run();
    }

    @Override
    protected void deleteEntity() {
        new CreatorService().deleteCreator(entity.getId());
        MainApp.showSuccess("Creator deleted.");
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Are you sure you want to delete " + entity.getName() + "?";
    }
}