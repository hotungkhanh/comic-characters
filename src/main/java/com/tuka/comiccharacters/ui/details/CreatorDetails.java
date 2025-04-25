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
        super.showDetailsDialog(600, 620);
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
        row = addNavigableListPanel(infoPanel, "Credited Characters", sortedCharacters, ComicCharacter::toString, this::navigateToCharacter, row);

        // Add issues panel
        List<Issue> sortedIssues = getSortedIssues();
        row = addNavigableListPanel(infoPanel, "Credited Issues", sortedIssues, Issue::toString, this::navigateToIssue, row);

        // Create the main scrollable panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        return mainPanel;
    }

    private List<ComicCharacter> getSortedCharacters() {
        return entity.getCreditedCharacters().stream().sorted(Comparator.comparing(ComicCharacter::getName, String.CASE_INSENSITIVE_ORDER)).toList();
    }

    private List<Issue> getSortedIssues() {
        return entity.getIssueCreators().stream().map(IssueCreator::getIssue).distinct().sorted(Comparator.comparing(Issue::toString, String.CASE_INSENSITIVE_ORDER)).toList();
    }

    private void navigateToCharacter(ComicCharacter character) {
        ComicCharacter fetched = characterService.getByIdWithDetails(character.getId());
        if (fetched != null) {
            currentDialog.dispose();
            new CharacterDetails(parent, fetched, refreshCallback).showDetailsDialog();
        } else {
            MainApp.showError("Could not load character details.");
        }
    }

    private void navigateToIssue(Issue issue) {
        Issue fetched = issueService.getByIdWithDetails(issue.getId());
        if (fetched != null) {
            currentDialog.dispose();
            new IssueDetails(parent, fetched, refreshCallback).showDetailsDialog();
        } else {
            MainApp.showError("Could not load issue details.");
        }
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
