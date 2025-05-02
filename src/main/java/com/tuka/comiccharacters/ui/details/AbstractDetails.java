package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.service.IssueService;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.ui.MainApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractDetails<T> {

    protected final Component parent;
    protected final T entity;
    protected final Runnable refreshCallback;
    protected final GridBagConstraints gbc;
    protected JDialog currentDialog;

    public AbstractDetails(Component parent, T entity, Runnable refreshCallback) {
        this.parent = parent;
        this.entity = entity;
        this.refreshCallback = refreshCallback;
        this.gbc = defaultGbc();
    }

    private static <T> JList<T> createSelectableList(Function<T, String> nameExtractor, DefaultListModel<T> listModel) {
        JList<T> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer((list1, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(nameExtractor.apply(value));
            if (isSelected) {
                label.setBackground(list1.getSelectionBackground());
                label.setForeground(list1.getSelectionForeground());
                label.setOpaque(true);
            }
            return label;
        });
        return list;
    }

    private static JPanel getErrorPanel() {
        JPanel errorPanel = new JPanel(new BorderLayout());

        // Create a label with HTML to allow text wrapping
        JLabel errorLabel = new JLabel("<html><div style='text-align: center;'>Image not available</div></html>");
        errorLabel.setHorizontalAlignment(JLabel.CENTER);
        errorLabel.setVerticalAlignment(JLabel.CENTER);

        // Set a minimum size to ensure the text has enough space
        errorPanel.setPreferredSize(new Dimension(180, 100));
        errorPanel.add(errorLabel, BorderLayout.CENTER);
        return errorPanel;
    }

    public void showDetailsDialog() {
        // Override in child classes to set custom window size
        showDetailsDialog(400, 300, "");
    }

    public void showDetailsDialog(int windowWidth, int windowHeight, String entityString) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), getTitle(), true);
        this.currentDialog = dialog;
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(windowWidth, windowHeight);
        dialog.setLocationRelativeTo(parent);

        dialog.add(getMainPanel(dialog), BorderLayout.CENTER);
        dialog.add(getButtonPanel(dialog, entityString), BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    protected JPanel getButtonPanel(JDialog dialog, String entityString) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Edit " + entityString);
        JButton deleteButton = new JButton("Delete " + entityString);

        editButton.addActionListener(e -> {
            dialog.dispose();
            showEditDialog();
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(parent, getDeleteConfirmationMessage(), "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteEntity();
                dialog.dispose();
                refreshCallback.run();
            }
        });

        panel.add(editButton);
        panel.add(deleteButton);
        return panel;
    }

    /**
     * Create a standard two-panel layout with an image panel on the left (if imageUrl is provided)
     * and the information panel on the right.
     *
     * @param dialog    The parent dialog
     * @param infoPanel The panel containing the entity information
     * @param imageUrl  The URL of the image to display, or null if no image
     * @return A panel with the standard layout
     */
    protected JPanel createStandardLayout(JDialog dialog, JPanel infoPanel, String imageUrl) {
        // Create the main scrollable panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Add image panel on the left if imageUrl is not null
        if (imageUrl != null && !imageUrl.isEmpty()) {
            JPanel imagePanel = createImagePanel(imageUrl);
            mainPanel.add(imagePanel, BorderLayout.WEST);
        }

        mainPanel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        return mainPanel;
    }

    protected abstract JPanel getMainPanel(JDialog dialog);

    protected abstract String getTitle();

    protected abstract void showEditDialog();

    // ===== Common UI Component Methods =====

    protected abstract void deleteEntity();

    protected abstract String getDeleteConfirmationMessage();

    protected int addLabelValue(JPanel panel, String label, String value, int row) {
        if (value == null || value.isBlank()) return row;

        panel.add(createLabel(label, row, 0, false), labelConstraints(row));
        panel.add(new JLabel(value), valueConstraints(row, GridBagConstraints.HORIZONTAL, 1.0));
        return row + 1;
    }

    protected int addTextArea(JPanel panel, String label, String text, int row, int rows) {
        if (text == null || text.isBlank()) return row;

        panel.add(createLabel(label, row, 0, false), labelConstraints(row));

        JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setBackground(panel.getBackground());
        textArea.setBorder(null);
        textArea.setFont(UIManager.getFont("Label.font"));
        textArea.setRows(rows);

        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, valueConstraints(row, GridBagConstraints.BOTH, 0.1));
        gbc.weighty = 0; // Reset
        return row + 1;
    }

    protected JPanel createMainInfoPanel() {
        return new JPanel(new GridBagLayout());
    }

    /**
     * Creates a panel with an image from a URL
     *
     * @param imageUrl The URL of the image to display
     * @return A panel containing the image
     */
    protected JPanel createImagePanel(String imageUrl) {
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(200, 300));
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));

        try {
            // Try to load the image from the URL
            ImageIcon originalIcon = new ImageIcon(new URL(imageUrl));
            Image originalImage = originalIcon.getImage();

            // Wait for the image to load completely
            if (originalIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                throw new Exception("Image failed to load completely");
            }

            // Check if image is valid
            if (originalImage.getWidth(null) <= 0 || originalImage.getHeight(null) <= 0) {
                throw new Exception("Invalid image dimensions");
            }

            // Scale the image to fit the panel while maintaining aspect ratio
            Image scaledImage = getScaledImage(originalImage, 180, 280);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            JLabel imageLabel = new JLabel(scaledIcon);
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePanel.add(imageLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            // If loading fails, show a placeholder with wrapped text
            JPanel errorPanel = getErrorPanel();

            imagePanel.add(errorPanel, BorderLayout.CENTER);
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
    protected Image getScaledImage(Image image, int maxWidth, int maxHeight) {
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

    // ===== Navigation Methods =====

    /**
     * Navigates to Character details screen
     */
    protected void navigateToCharacter(ComicCharacter character, CharacterService characterService) {
        ComicCharacter fetched = characterService.getByIdWithDetails(character.getId());
        if (fetched != null) {
            currentDialog.dispose();
            new CharacterDetails(parent, fetched, refreshCallback).showDetailsDialog();
        } else {
            MainApp.showError("Could not load character details.");
        }
    }

    /**
     * Navigates to Creator details screen
     */
    protected void navigateToCreator(Creator creator, CreatorService creatorService) {
        Creator fetched = creatorService.getByIdWithDetails(creator.getId());
        if (fetched != null) {
            currentDialog.dispose();
            new CreatorDetails(parent, fetched, refreshCallback).showDetailsDialog();
        } else {
            MainApp.showError("Could not load creator details.");
        }
    }

    /**
     * Navigates to Issue details screen
     */
    protected void navigateToIssue(Issue issue, IssueService issueService) {
        Issue fetched = issueService.getByIdWithDetails(issue.getId());
        if (fetched != null) {
            currentDialog.dispose();
            new IssueDetails(parent, fetched, refreshCallback).showDetailsDialog();
        } else {
            MainApp.showError("Could not load issue details.");
        }
    }

    // ===== Navigable Lists =====

    /**
     * Adds a clickable publisher row that navigates to publisher details when clicked
     *
     * @param panel            The panel to add the clickable publisher to
     * @param row              The current row in the grid layout
     * @param publisher        The publisher entity to display and navigate to
     * @param publisherService The service used to fetch the full publisher details
     * @return The next available row number
     */
    protected int addClickablePublisher(JPanel panel, int row, Publisher publisher, PublisherService publisherService) {
        if (publisher == null) {
            return row;
        }

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Publisher fetchedPublisher = publisherService.getByIdWithDetails(publisher.getId());
                if (fetchedPublisher != null) {
                    currentDialog.dispose();
                    new PublisherDetails(parent, fetchedPublisher, refreshCallback).showDetailsDialog();
                } else {
                    MainApp.showError("Could not load publisher details.");
                }
            }
        };

        return addClickableLabel(panel, "Publisher:", publisher.getName(), row, mouseAdapter, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    protected int addClickableLabel(JPanel panel, String labelText, String valueText, int row, MouseAdapter mouseAdapter, Cursor cursor) {
        if (valueText == null || valueText.isBlank()) return row;

        panel.add(createLabel(labelText, row, 0, false), labelConstraints(row));

        JLabel valueLabel = new JLabel(valueText);
        if (mouseAdapter != null) {
            valueLabel.setForeground(Color.BLUE);
            valueLabel.setCursor(cursor);
            valueLabel.addMouseListener(mouseAdapter);
        }

        panel.add(valueLabel, valueConstraints(row, GridBagConstraints.HORIZONTAL, 1.0));
        return row + 1;
    }

    /**
     * Creates a clickable list panel with items that can be navigated to on double-click
     */
    protected <E> JPanel createClickableListPanel(String title, List<E> items, Function<E, String> nameExtractor, Consumer<E> onDoubleClick) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        DefaultListModel<E> listModel = new DefaultListModel<>();
        items.forEach(listModel::addElement);

        JList<E> list = createSelectableList(nameExtractor, listModel);

        list.addMouseListener(getListDoubleClickListener(items, onDoubleClick));

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(200, 120));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Adds a clickable list panel to the parent panel and updates the grid position
     */
    protected <E> int addNavigableListPanel(JPanel parentPanel, String title, List<E> items, Function<E, String> nameExtractor, Consumer<E> onDoubleClick, int row) {
        if (items == null || items.isEmpty()) return row;

        JPanel panel = createClickableListPanel(title, items, nameExtractor, onDoubleClick);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        parentPanel.add(panel, gbc);
        return row;
    }

    /**
     * Creates a mouse adapter for handling double-click navigation on lists
     */
    protected <E> MouseAdapter getListDoubleClickListener(List<E> items, Consumer<E> onDoubleClick) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JList<?> list = (JList<?>) e.getSource();
                    int selectedIndex = list.getSelectedIndex();
                    if (selectedIndex >= 0 && selectedIndex < items.size()) {
                        onDoubleClick.accept(items.get(selectedIndex));
                    }
                }
            }
        };
    }

    // ===== Constraint and Layout Helper Methods =====

    private GridBagConstraints defaultGbc() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        return c;
    }

    private GridBagConstraints labelConstraints(int row) {
        GridBagConstraints c = (GridBagConstraints) gbc.clone();
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        return c;
    }

    private GridBagConstraints valueConstraints(int row, int fill, double weightY) {
        GridBagConstraints c = (GridBagConstraints) gbc.clone();
        c.gridx = 1;
        c.gridy = row;
        c.fill = fill;
        c.weighty = weightY;
        return c;
    }

    private JLabel createLabel(String text, int row, int col, boolean bold) {
        JLabel label = new JLabel(text);
        if (bold) {
            label.setFont(label.getFont().deriveFont(Font.BOLD));
        }
        return label;
    }
}
