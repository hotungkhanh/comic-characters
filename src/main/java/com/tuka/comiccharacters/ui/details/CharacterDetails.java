package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.ui.MainApp;
import com.tuka.comiccharacters.ui.form.CharacterForm;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class CharacterDetails extends AbstractDetails<ComicCharacter> {

    public CharacterDetails(Component parent, ComicCharacter character, Runnable refreshCallback) {
        super(parent, character, refreshCallback);
    }

    public void showDetailsDialog() {
        super.showDetailsDialog(600, 750);
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel infoPanel = createMainInfoPanel();
        int row = 0;

        row = addLabelValue(infoPanel, "Name:", entity.getName(), row);
        row = addLabelValue(infoPanel, "Alias:", entity.getAlias(), row);
        row = addLabelValue(infoPanel, "Publisher:", entity.getPublisher() != null ? entity.getPublisher().getName() : null, row);

        row = addTextArea(infoPanel, "Overview:", entity.getOverview(), row, 5);

        List<Creator> sortedCreators = entity.getCreators().stream()
                .sorted(Comparator.comparing(Creator::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        infoPanel.add(createListPanel("Creators", sortedCreators, Creator::getName, 100), gbc);

        // First Appearance
        if (entity.getFirstAppearance() != null) {
            gbc.gridx = 0;
            gbc.gridy = row++;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JLabel firstAppearanceLabel = new JLabel("First appearance: " + entity.getFirstAppearance().toString());
            infoPanel.add(firstAppearanceLabel, gbc);
        }

        List<Issue> sortedIssues = entity.getIssues().stream()
                .sorted(Comparator.comparing(Issue::toString, String.CASE_INSENSITIVE_ORDER))
                .toList();
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        infoPanel.add(createListPanel("Appears in", sortedIssues, Issue::toString, 150), gbc);

        mainPanel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        return mainPanel;
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
        new CharacterService().deleteCharacter(entity.getId());
        MainApp.showSuccess("Character deleted.");
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Delete this character?";
    }
}
