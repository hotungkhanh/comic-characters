package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.ui.MainApp;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class CharacterDetails extends AbstractDetails<ComicCharacter> {

    public CharacterDetails(Component parent, ComicCharacter character, Runnable refreshCallback) {
        super(parent, character, refreshCallback);
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
        row = addLabelValue(infoPanel, "Alias:", entity.getAlias(), row);
        row = addLabelValue(infoPanel, "Publisher:", entity.getPublisher() != null ? entity.getPublisher().getName() : null, row);

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

            infoPanel.add(overviewArea, gbc);
            row++;
        }

        // Creators Panel
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;

        JPanel creatorsPanel = new JPanel(new BorderLayout());
        creatorsPanel.setBorder(BorderFactory.createTitledBorder("Creators"));

        List<Creator> sortedCreators = entity.getCreators().stream()
                .sorted(Comparator.comparing(Creator::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        DefaultListModel<String> creatorListModel = new DefaultListModel<>();
        for (Creator creator : sortedCreators) {
            creatorListModel.addElement(creator.getName());
        }

        JList<String> creatorList = new JList<>(creatorListModel);
        creatorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        creatorsPanel.add(new JScrollPane(creatorList), BorderLayout.CENTER);
        creatorsPanel.setPreferredSize(new Dimension(400, 100));
        infoPanel.add(creatorsPanel, gbc);

        // First Appearance
        row = addLabelValue(infoPanel, "First appearance:",
                entity.getFirstAppearance() != null ? entity.getFirstAppearance().toString() : null, row);

        // Issues Panel
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;

        JPanel issuesPanel = new JPanel(new BorderLayout());
        issuesPanel.setBorder(BorderFactory.createTitledBorder("Appears in"));

        List<Issue> sortedIssues = entity.getIssues().stream()
                .sorted(Comparator.comparing(Issue::toString, String.CASE_INSENSITIVE_ORDER))
                .toList();

        DefaultListModel<Issue> issueListModel = new DefaultListModel<>();
        for (Issue issue : sortedIssues) {
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

        return row + 1;
    }

    @Override
    protected String getTitle() {
        return "Character Details";
    }

    @Override
    protected void showEditDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Edit Character", true);
        // CharacterForm panel = new CharacterForm(entity);
        // dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        refreshCallback.run();
    }

    @Override
    protected void deleteEntity() {
        new CharacterService().deleteCharacter(entity.getId());
        MainApp.showSuccess("Character deleted.");
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Delete this character?";
    }
}
