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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class IssueDetails extends AbstractDetails<Issue> {

    private final IssueService issueService = new IssueService();
    private final PublisherService publisherService = new PublisherService();
    private final CreatorService creatorService = new CreatorService();
    private final CharacterService characterService = new CharacterService();
    private JDialog detailsDialog;

    public IssueDetails(Component parent, Issue issue, Runnable refreshCallback) {
        super(parent, issue, refreshCallback);
    }

    public void showDetailsDialog() {
        super.showDetailsDialog(800, 900);
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        this.detailsDialog = dialog;

        JPanel infoPanel = createMainInfoPanel();
        int row = 0;
        row = addLabelValue(infoPanel, "Issue:", entity.toString(), row);

        // Publisher
        row = addClickablePublisher(infoPanel, row, (entity.getSeries() != null) ? entity.getSeries().getPublisher() : null);

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

        // Creators
        List<IssueCreator> issueCreators = List.copyOf(entity.getIssueCreators()); // Convert Set to List
        if (!issueCreators.isEmpty()) {
            List<Creator> creators = issueCreators.stream()
                    .map(IssueCreator::getCreator)
                    .collect(Collectors.toList());
            JList<String> creatorsList = createStringList(creators, this::getCreatorNameAndRoles); // Use method reference
            creatorsList.addMouseListener(getListDoubleClickListener(creators, creator -> {
                Creator fetched = creatorService.getCreatorByIdWithDetails(creator.getId());
                if (fetched != null) {
                    detailsDialog.dispose();
                    new CreatorDetails(parent, fetched, refreshCallback).showDetailsDialog();
                } else {
                    MainApp.showError("Could not load creator details.");
                }
            }));
            row = addTitledListPanel(infoPanel, "Creators", creatorsList, row, 100);
        }

        // Characters
        List<ComicCharacter> characters = List.copyOf(entity.getCharacters()); // Convert Set to List
        if (!characters.isEmpty()) {
            JList<String> charactersList = createStringList(characters, ComicCharacter::getName);
            charactersList.addMouseListener(getListDoubleClickListener(characters, character -> {
                ComicCharacter fetched = characterService.getCharacterByIdWithDetails(character.getId());
                if (fetched != null) {
                    detailsDialog.dispose();
                    new CharacterDetails(parent, fetched, refreshCallback).showDetailsDialog();
                } else {
                    MainApp.showError("Could not load character details.");
                }
            }));
            row = addTitledListPanel(infoPanel, "Characters", charactersList, row, 100);
        }

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(new JScrollPane(infoPanel), BorderLayout.NORTH);
        return mainPanel;
    }

    private String getCreatorNameAndRoles(Creator creator) {
        String rolesText = entity.getIssueCreators().stream()
                .filter(ic -> ic.getCreator().equals(creator))
                .flatMap(ic -> ic.getRoles().stream())
                .map(Role::name)
                .collect(Collectors.joining(", "));
        return creator.getName() + (rolesText.isEmpty() ? "" : " (" + rolesText + ")");
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
                    Publisher fetchedPublisher = publisherService.getPublisherByIdWithSeriesAndCharacters(publisher.getId());
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
        return "Issue Details";
    }

    @Override
    protected void showEditDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Edit Issue", true);
        IssueForm issueForm = new IssueForm(entity, refreshCallback, dialog);
        dialog.setContentPane(issueForm);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    @Override
    protected void deleteEntity() {
        issueService.deleteIssue(entity.getId());
        MainApp.showSuccess("Issue deleted.");
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Are you sure you want to delete this issue?";
    }
}
