package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.IssueCreator;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.service.IssueService;
import com.tuka.comiccharacters.ui.MainApp;
import com.tuka.comiccharacters.ui.form.CreatorForm;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class CreatorDetails extends AbstractDetails<Creator> {
    private final CreatorService creatorService;
    private final CharacterService characterService;
    private final IssueService issueService;

    public CreatorDetails(Component parent, Creator creator, Runnable refreshCallback) {
        super(parent, creator, refreshCallback);
        this.creatorService = new CreatorService();
        this.characterService = new CharacterService();
        this.issueService = new IssueService();
    }

    @Override
    public void showDetailsDialog() {
        super.showDetailsDialog(750, 620, "Creator");  // Made wider to accommodate the image
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        JPanel infoPanel = createMainInfoPanel();
        int row = 0;

        // Basic information
        row = addLabelValue(infoPanel, "Name:", entity.getName(), row);
        row = addTextArea(infoPanel, "Overview:", entity.getOverview(), row, 5);

        // Add characters panel
        List<ComicCharacter> sortedCharacters = getSortedCharacters();
        row = addNavigableListPanel(infoPanel, "Credited Characters", sortedCharacters, ComicCharacter::toString, character -> navigateToCharacter(character, characterService), row);

        // Add issues panel
        List<Issue> sortedIssues = getSortedIssues();
        row = addNavigableListPanel(infoPanel, "Credited Issues", sortedIssues, Issue::toString, issue -> navigateToIssue(issue, issueService), row);

        // Create standard layout with image and info panel
        return createStandardLayout(dialog, infoPanel, entity.getImageUrl());
    }

    private List<ComicCharacter> getSortedCharacters() {
        return entity.getCreditedCharacters().stream().sorted(Comparator.comparing(ComicCharacter::getName, String.CASE_INSENSITIVE_ORDER)).toList();
    }

    private List<Issue> getSortedIssues() {
        return entity.getIssueCreators().stream().map(IssueCreator::getIssue).distinct().sorted(Comparator.comparing(Issue::toString, String.CASE_INSENSITIVE_ORDER)).toList();
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
        creatorService.delete(entity.getId());
        MainApp.showSuccess("Creator deleted.");
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Are you sure you want to delete " + entity.getName() + "?";
    }
}
