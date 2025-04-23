package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.IssueCreator;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.ui.MainApp;
import com.tuka.comiccharacters.ui.form.CreatorForm;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CreatorDetails extends AbstractDetails<Creator> {

    public CreatorDetails(Component parent, Creator creator, Runnable refreshCallback) {
        super(parent, creator, refreshCallback);
    }

    public void showDetailsDialog() {
        super.showDetailsDialog(600, 700);
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel infoPanel = createMainInfoPanel();
        int row = 0;

        row = addLabelValue(infoPanel, "Name:", entity.getName(), row);

        row = addTextArea(infoPanel, "Overview:", entity.getOverview(), row, 5);

        List<ComicCharacter> sortedCharacters = entity.getCreditedCharacters().stream()
                .sorted(Comparator.comparing(ComicCharacter::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        infoPanel.add(createListPanel("Credited Characters", sortedCharacters, ComicCharacter::toString, 100), gbc);

        Set<Issue> creditedIssues = entity.getIssueCreators().stream()
                .map(IssueCreator::getIssue)
                .collect(Collectors.toSet());
        List<Issue> sortedCreditedIssues = List.copyOf(creditedIssues); // Convert Set to List for sorting
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        infoPanel.add(createListPanel("Credited Issues", sortedCreditedIssues, Issue::toString, 150), gbc);

        mainPanel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        return mainPanel;
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
        new CreatorService().deleteCreator(entity.getId());
        MainApp.showSuccess("Creator deleted.");
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Are you sure you want to delete " + entity.getName() + "?";
    }
}
