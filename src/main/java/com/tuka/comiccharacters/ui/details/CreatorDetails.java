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
import java.net.URL;
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
        super.showDetailsDialog(750, 620);  // Made wider to accommodate the image
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

        // Add image panel on the left if imageUrl is not null
        if (entity.getImageUrl() != null && !entity.getImageUrl().isEmpty()) {
            JPanel imagePanel = createImagePanel(entity.getImageUrl());
            mainPanel.add(imagePanel, BorderLayout.WEST);
        }

        mainPanel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        return mainPanel;
    }

    /**
     * Creates a panel with the creator's image
     *
     * @param imageUrl The URL of the image to display
     * @return A panel containing the image
     */
    private JPanel createImagePanel(String imageUrl) {
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(200, 300));
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));

        try {
            // Try to load the image from the URL
            ImageIcon originalIcon = new ImageIcon(new URL(imageUrl));
            Image originalImage = originalIcon.getImage();

            // Scale the image to fit the panel while maintaining aspect ratio
            Image scaledImage = getScaledImage(originalImage, 180, 280);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            JLabel imageLabel = new JLabel(scaledIcon);
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePanel.add(imageLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            // If loading fails, show a placeholder
            JLabel errorLabel = new JLabel("Image not available");
            errorLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePanel.add(errorLabel, BorderLayout.CENTER);
        }

        return imagePanel;
    }

    /**
     * Scales an image while maintaining its aspect ratio
     *
     * @param image     The image to scale
     * @param maxWidth  The maximum width of the scaled image
     * @param maxHeight The maximum height of the scaled image
     * @return The scaled image
     */
    private Image getScaledImage(Image image, int maxWidth, int maxHeight) {
        int originalWidth = image.getWidth(null);
        int originalHeight = image.getHeight(null);

        if (originalWidth <= 0 || originalHeight <= 0) {
            return image; // Cannot scale
        }

        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int scaledWidth = (int) (originalWidth * ratio);
        int scaledHeight = (int) (originalHeight * ratio);

        return image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
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
