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
import java.net.URL;
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
        super.showDetailsDialog(750, 800); // Made wider to accommodate the image
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
        row = addNavigableListPanel(infoPanel, "Creators", sortedCreators, Creator::getName, this::navigateToCreator, row);

        // First Appearance
        row = addFirstAppearance(infoPanel, row);

        // Issues list
        List<Issue> sortedIssues = getSortedIssues();
        row = addNavigableListPanel(infoPanel, "Appears in", sortedIssues, Issue::toString, this::navigateToIssue, row);

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
     * Creates a panel with the character's image
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
                navigateToIssue(firstAppearance);
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

    private void navigateToCreator(Creator creator) {
        Creator fetched = creatorService.getByIdWithDetails(creator.getId());
        if (fetched != null) {
            currentDialog.dispose();
            new CreatorDetails(parent, fetched, refreshCallback).showDetailsDialog();
        } else {
            MainApp.showError("Could not load creator details.");
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
