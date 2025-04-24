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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class CreatorDetails extends AbstractDetails<Creator> {

    private final CharacterService characterService = new CharacterService();
    private final IssueService issueService = new IssueService();
    private JDialog detailsDialog;

    public CreatorDetails(Component parent, Creator creator, Runnable refreshCallback) {
        super(parent, creator, refreshCallback);
    }

    public void showDetailsDialog() {
        super.showDetailsDialog(600, 620);
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        this.detailsDialog = dialog;

        JPanel infoPanel = createMainInfoPanel();
        int row = 0;

        row = addLabelValue(infoPanel, "Name:", entity.getName(), row);
        if (entity.getOverview() != null && !entity.getOverview().isBlank()) {
            row = addTextArea(infoPanel, "Overview:", entity.getOverview(), row, 5);
        }

        List<ComicCharacter> sortedCharacters = entity.getCreditedCharacters().stream()
                .sorted(Comparator.comparing(ComicCharacter::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        List<Issue> sortedIssues = entity.getIssueCreators().stream()
                .map(IssueCreator::getIssue)
                .distinct()
                .sorted(Comparator.comparing(Issue::toString, String.CASE_INSENSITIVE_ORDER))
                .toList();

        JPanel characterPanel = createClickableListPanel(
                "Credited Characters",
                sortedCharacters,
                ComicCharacter::toString,
                selected -> {
                    ComicCharacter fetched = characterService.getByIdWithDetails(selected.getId());
                    if (fetched != null) {
                        detailsDialog.dispose();
                        new CharacterDetails(parent, fetched, refreshCallback).showDetailsDialog();
                    } else {
                        MainApp.showError("Could not load character details.");
                    }
                }
        );

        JPanel issuePanel = createClickableListPanel(
                "Credited Issues",
                sortedIssues,
                Issue::toString,
                selected -> {
                    Issue fetched = issueService.getByIdWithDetails(selected.getId());
                    if (fetched != null) {
                        detailsDialog.dispose();
                        new IssueDetails(parent, fetched, refreshCallback).showDetailsDialog();
                    } else {
                        MainApp.showError("Could not load issue details.");
                    }
                }
        );

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        infoPanel.add(characterPanel, gbc);

        gbc.gridy = row++;
        infoPanel.add(issuePanel, gbc);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        return mainPanel;
    }

    private <T> JPanel createClickableListPanel(
            String title,
            List<T> items,
            Function<T, String> nameExtractor,
            java.util.function.Consumer<T> onDoubleClick
    ) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        DefaultListModel<T> listModel = new DefaultListModel<>();
        items.forEach(listModel::addElement);

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

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    T selected = list.getSelectedValue();
                    if (selected != null) {
                        onDoubleClick.accept(selected);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(200, 120));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
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
        new CreatorService().delete(entity.getId());
        MainApp.showSuccess("Creator deleted.");
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Are you sure you want to delete " + entity.getName() + "?";
    }
}
