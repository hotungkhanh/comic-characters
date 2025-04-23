package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.service.IssueService;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.ui.form.IssueForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class IssueDetails extends AbstractDetails<Issue> {

    private final IssueService issueService = new IssueService();
    private final PublisherService publisherService = new PublisherService();
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

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel infoPanel = createMainInfoPanel();

        int row = 0;
        row = addLabelValue(infoPanel, "Issue:", entity.toString(), row);

        // Publisher
        String publisherText = (entity.getSeries() != null && entity.getSeries().getPublisher() != null)
                ? entity.getSeries().getPublisher().getName()
                : "None";

        Publisher issuePublisher = (entity.getSeries() != null) ? entity.getSeries().getPublisher() : null;

        JLabel publisherLabel = new JLabel(publisherText);
        if (issuePublisher != null) {
            publisherLabel.setForeground(Color.BLUE);
            publisherLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            publisherLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    detailsDialog.dispose();
                    Publisher fetchedPublisher = publisherService.getPublisherByIdWithSeriesAndCharacters(issuePublisher.getId());
                    new PublisherDetails(parent, fetchedPublisher, refreshCallback).showDetailsDialog();
                }
            });
        }

        GridBagConstraints labelGbc = (GridBagConstraints) gbc.clone();
        labelGbc.gridx = 0;
        labelGbc.gridy = row;
        labelGbc.weightx = 0;
        labelGbc.fill = GridBagConstraints.NONE;
        infoPanel.add(new JLabel("Publisher:"), labelGbc);

        GridBagConstraints valueGbc = (GridBagConstraints) gbc.clone();
        valueGbc.gridx = 1;
        valueGbc.gridy = row;
        infoPanel.add(publisherLabel, valueGbc);
        row++;

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
        String creatorsText = entity.getIssueCreators().stream()
                .map(ic -> ic.getCreator().getName() + " (" + ic.getRoles().stream().map(Enum::name).collect(Collectors.joining(", ")) + ")")
                .collect(Collectors.joining(", "));
        row = addTextArea(infoPanel, "Creators:", creatorsText.isEmpty() ? "None" : creatorsText, row, 3);

        // Characters
        String charactersText = entity.getCharacters().stream()
                .map(ComicCharacter::getName)
                .collect(Collectors.joining(", "));
        row = addTextArea(infoPanel, "Characters:", charactersText.isEmpty() ? "None" : charactersText, row, 3);

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        return mainPanel;
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
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Are you sure you want to delete this issue?";
    }
}
