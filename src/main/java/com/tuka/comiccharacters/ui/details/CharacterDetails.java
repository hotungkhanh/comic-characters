package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.service.IssueService;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.ui.MainApp;
import com.tuka.comiccharacters.ui.form.CharacterForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

public class CharacterDetails extends AbstractDetails<ComicCharacter> {

    private final CharacterService characterService;
    private final CreatorService creatorService;
    private final IssueService issueService;
    private final PublisherService publisherService;

    public CharacterDetails(Component parent, ComicCharacter character, Runnable refreshCallback) {
        super(parent, character, refreshCallback);
        this.characterService = new CharacterService();
        this.creatorService = new CreatorService();
        this.issueService = new IssueService();
        this.publisherService = new PublisherService();
    }

    @Override
    public void showDetailsDialog() {
        super.showDetailsDialog(750, 800, "Character");
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        JPanel infoPanel = createMainInfoPanel();
        int row = 0;

        // Basic information
        row = addLabelValue(infoPanel, "Name:", entity.getName(), row);
        row = addLabelValue(infoPanel, "Alias:", entity.getAlias(), row);

        // Publisher with click action
        row = addClickablePublisher(infoPanel, row);

        // Overview
        row = addTextArea(infoPanel, "Overview:", entity.getOverview(), row, 5);

        // Creators list
        List<Creator> sortedCreators = getSortedCreators();
        row = addNavigableListPanel(infoPanel, "Creators", sortedCreators, Creator::getName, creator -> navigateToCreator(creator, creatorService), row);

        // First Appearance
        row = addFirstAppearance(infoPanel, row);

        // Issues list
        List<Issue> sortedIssues = getSortedIssues();
        row = addNavigableListPanel(infoPanel, "Appears in", sortedIssues, Issue::toString, issue -> navigateToIssue(issue, issueService), row);

        // Create standard layout with image and info panel
        return createStandardLayout(dialog, infoPanel, entity.getImageUrl());
    }

    private List<Creator> getSortedCreators() {
        return entity.getCreators().stream().sorted(Comparator.comparing(Creator::getName, String.CASE_INSENSITIVE_ORDER)).toList();
    }

    private List<Issue> getSortedIssues() {
        return entity.getIssues().stream().sorted(Comparator.comparing(Issue::toString, String.CASE_INSENSITIVE_ORDER)).toList();
    }

    private int addClickablePublisher(JPanel panel, int row) {
        return addClickablePublisher(panel, row, entity.getPublisher(), publisherService);
    }

    private int addFirstAppearance(JPanel panel, int row) {
        Issue firstAppearance = entity.getFirstAppearance();
        if (firstAppearance == null) return row;

        JPanel firstAppearancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel label = new JLabel("First appearance:");
        firstAppearancePanel.add(label);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToIssue(firstAppearance, issueService);
            }
        };

        JLabel issueLabel = new JLabel(firstAppearance.toString());
        issueLabel.setForeground(Color.BLUE);
        issueLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        issueLabel.addMouseListener(mouseAdapter);
        firstAppearancePanel.add(issueLabel);

        GridBagConstraints c = (GridBagConstraints) gbc.clone();
        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(firstAppearancePanel, c);

        return row;
    }

    @Override
    protected String getTitle() {
        return "Character Details";
    }

    @Override
    protected void showEditDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Edit Character", true);
        CharacterForm panel = new CharacterForm(entity);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        refreshCallback.run();
    }

    @Override
    protected void deleteEntity() {
        characterService.delete(entity.getId());
        MainApp.showSuccess("Character deleted.");
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Delete this character?";
    }
}
