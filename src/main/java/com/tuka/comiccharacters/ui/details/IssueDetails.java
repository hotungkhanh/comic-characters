package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.*;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.service.IssueService;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.ui.MainApp;
import com.tuka.comiccharacters.ui.form.IssueForm;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class IssueDetails extends AbstractDetails<Issue> {

    private final IssueService issueService;
    private final PublisherService publisherService;
    private final CreatorService creatorService;
    private final CharacterService characterService;

    public IssueDetails(Component parent, Issue issue, Runnable refreshCallback) {
        super(parent, issue, refreshCallback);
        this.issueService = new IssueService();
        this.publisherService = new PublisherService();
        this.creatorService = new CreatorService();
        this.characterService = new CharacterService();
    }

    @Override
    public void showDetailsDialog() {
        super.showDetailsDialog(800, 700, "Issue");
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        JPanel infoPanel = createMainInfoPanel();
        int row = 0;

        // Basic information
        row = addLabelValue(infoPanel, "Issue:", entity.toString(), row);

        // Publisher (if available)
        row = addClickablePublisher(infoPanel, row);

        // Overview
        row = addTextArea(infoPanel, "Overview:", entity.getOverview(), row, 3);

        // Release Date
        if (entity.getReleaseDate() != null) {
            row = addLabelValue(infoPanel, "Release Date:", entity.getReleaseDate().format(DateTimeFormatter.ISO_LOCAL_DATE), row);
        }

        // Price
        if (entity.getPriceUsd() != null) {
            row = addLabelValue(infoPanel, "Price (USD):", String.format("$%.2f", entity.getPriceUsd()), row);
        }

        // Creators list
        row = addCreatorsList(infoPanel, row);

        // Characters list
        row = addCharactersList(infoPanel, row);

        // If there's an image URL, use the standard layout with image on the left, otherwise use the original layout
        if (entity.getImageUrl() != null && !entity.getImageUrl().isEmpty()) {
            return createStandardLayout(dialog, infoPanel, entity.getImageUrl());
        } else {
            // Create scrollable panel (original layout)
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
            return mainPanel;
        }
    }

    private int addCreatorsList(JPanel panel, int row) {
        if (entity.getIssueCreators() == null || entity.getIssueCreators().isEmpty()) {
            return row;
        }

        List<Creator> creators = entity.getIssueCreators().stream().map(IssueCreator::getCreator).collect(Collectors.toList());

        return addNavigableListPanel(panel, "Creators", creators, this::getCreatorNameAndRoles, this::navigateToCreator, row);
    }

    private int addCharactersList(JPanel panel, int row) {
        if (entity.getCharacters() == null || entity.getCharacters().isEmpty()) {
            return row;
        }

        List<ComicCharacter> characters = List.copyOf(entity.getCharacters());

        return addNavigableListPanel(panel, "Characters", characters, ComicCharacter::toString, this::navigateToCharacter, row);
    }

    private String getCreatorNameAndRoles(Creator creator) {
        String rolesText = entity.getIssueCreators().stream().filter(ic -> ic.getCreator().equals(creator)).flatMap(ic -> ic.getRoles().stream()).map(Role::name).collect(Collectors.joining(", "));
        return creator.getName() + (rolesText.isEmpty() ? "" : " (" + rolesText + ")");
    }

    private int addClickablePublisher(JPanel panel, int row) {
        Publisher publisher = (entity.getSeries() != null) ? entity.getSeries().getPublisher() : null;
        return addClickablePublisher(panel, row, publisher, publisherService);
    }

    private void navigateToCreator(Creator creator) {
        Creator fetched = creatorService.getByIdWithDetails(creator.getId());
        if (fetched != null) {
            currentDialog.dispose();
            new CreatorDetails(parent, fetched, refreshCallback).showDetailsDialog();
        } else {
            MainApp.showError("Could not load creator details.");
        }
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

    @Override
    protected String getTitle() {
        return "Issue Details";
    }

    @Override
    protected void showEditDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Edit Issue", true);
        IssueForm issueForm = new IssueForm(entity, refreshCallback, dialog);
        dialog.setContentPane(issueForm);
        dialog.pack();
        dialog.setSize(700, 900);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    @Override
    protected void deleteEntity() {
        issueService.delete(entity.getId());
        MainApp.showSuccess("Issue deleted.");
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Are you sure you want to delete this issue?";
    }
}
