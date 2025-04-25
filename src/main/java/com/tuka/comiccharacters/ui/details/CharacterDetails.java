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
import com.tuka.comiccharacters.ui.form.CharacterForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class CharacterDetails extends AbstractDetails<ComicCharacter> {

    private final CreatorService creatorService = new CreatorService();
    private final IssueService issueService = new IssueService();
    private final PublisherService publisherService = new PublisherService();
    private JDialog detailsDialog;

    public CharacterDetails(Component parent, ComicCharacter character, Runnable refreshCallback) {
        super(parent, character, refreshCallback);
    }

    @Override
    public void showDetailsDialog() {
        super.showDetailsDialog(600, 800);
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        this.detailsDialog = dialog;
        JPanel infoPanel = createMainInfoPanel();
        int row = 0;

        row = addLabelValue(infoPanel, "Name:", entity.getName(), row);
        row = addLabelValue(infoPanel, "Alias:", entity.getAlias(), row);

        // Publisher with click action
        row = addClickablePublisher(infoPanel, row, entity.getPublisher());

        row = addTextArea(infoPanel, "Overview:", entity.getOverview(), row, 5);

        // Clickable Creators list
        List<Creator> sortedCreators = entity.getCreators().stream()
                .sorted(Comparator.comparing(Creator::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
        JList<String> creatorsList = createStringList(sortedCreators, Creator::getName);
        creatorsList.addMouseListener(getListDoubleClickListener(sortedCreators, creator -> {
            Creator fetched = creatorService.getByIdWithDetails(creator.getId());
            if (fetched != null) {
                detailsDialog.dispose();
                new CreatorDetails(parent, fetched, refreshCallback).showDetailsDialog();
            } else {
                MainApp.showError("Could not load creator details.");
            }
        }));
        row = addTitledListPanel(infoPanel, "Creators", creatorsList, row, 100);

        // First Appearance with click action
        if (entity.getFirstAppearance() != null) {
            JPanel firstAppearancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            JLabel label = new JLabel("First appearance:");
            firstAppearancePanel.add(label);
            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    detailsDialog.dispose();
                    Issue fetchedIssue = issueService.getByIdWithDetails(entity.getFirstAppearance().getId());
                    new IssueDetails(parent, fetchedIssue, refreshCallback).showDetailsDialog();
                }
            };
            JLabel issueLabel = new JLabel(entity.getFirstAppearance().toString());
            issueLabel.setForeground(Color.BLUE);
            issueLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            issueLabel.addMouseListener(mouseAdapter);
            firstAppearancePanel.add(issueLabel);

            gbc.gridx = 0;
            gbc.gridy = row++;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            infoPanel.add(firstAppearancePanel, gbc);
        }

        // Clickable "Appears in" list
        List<Issue> sortedIssues = entity.getIssues().stream()
                .sorted(Comparator.comparing(Issue::toString, String.CASE_INSENSITIVE_ORDER))
                .toList();
        JList<String> appearsInList = createStringList(sortedIssues, Issue::toString);
        appearsInList.addMouseListener(getListDoubleClickListener(sortedIssues, issue -> {
            Issue fetched = issueService.getByIdWithDetails(issue.getId());
            if (fetched != null) {
                detailsDialog.dispose();
                new IssueDetails(parent, fetched, refreshCallback).showDetailsDialog();
            } else {
                MainApp.showError("Could not load issue details.");
            }
        }));
        row = addTitledListPanel(infoPanel, "Appears in", appearsInList, row, 150);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        return mainPanel;
    }

    private int addClickablePublisher(JPanel panel, int row, Publisher publisher) {
        MouseAdapter mouseAdapter = null;
        String publisherName = null;
        if (publisher != null) {
            publisherName = publisher.getName();
            mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    detailsDialog.dispose();
                    Publisher fetchedPublisher = publisherService.getByIdWithDetails(publisher.getId());
                    new PublisherDetails(parent, fetchedPublisher, refreshCallback).showDetailsDialog();
                }
            };
        }
        return addClickableLabel(panel, "Publisher:", publisherName, row, mouseAdapter, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private <T> MouseAdapter getListDoubleClickListener(List<T> items, Consumer<T> onDoubleClick) {
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

    private int addTitledListPanel(JPanel parentPanel, String title, JList<?> list, int row, int preferredHeight) {
        JPanel panel = createTitledPanel(createListPanel(list, preferredHeight), title);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        parentPanel.add(panel, gbc);
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
        new CharacterService().delete(entity.getId());
        MainApp.showSuccess("Character deleted.");
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Delete this character?";
    }
}
