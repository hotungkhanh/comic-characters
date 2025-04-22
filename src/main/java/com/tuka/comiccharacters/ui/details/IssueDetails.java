package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.service.IssueService;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.ui.MainApp;
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

    @Override
    public void showDetailsDialog() {
        detailsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), getTitle(), true);
        detailsDialog.setLayout(new BorderLayout(10, 10));
        detailsDialog.setSize(400, 300);
        detailsDialog.setLocationRelativeTo(parent);

        detailsDialog.add(getMainPanel(detailsDialog), BorderLayout.CENTER);
        detailsDialog.add(getButtonPanel(detailsDialog), BorderLayout.SOUTH);

        detailsDialog.setVisible(true);
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Reusable label constraints
        GridBagConstraints labelGbc = (GridBagConstraints) gbc.clone();
        labelGbc.gridx = 0;
        labelGbc.weightx = 0;
        labelGbc.fill = GridBagConstraints.NONE;

        // Reusable value constraints
        GridBagConstraints valueGbc = (GridBagConstraints) gbc.clone();
        valueGbc.gridx = 1;
        valueGbc.weightx = 1.0;
        valueGbc.fill = GridBagConstraints.HORIZONTAL;

        // Issue Title
        labelGbc.gridy = valueGbc.gridy = row++;
        infoPanel.add(new JLabel("Issue:"), labelGbc);
        infoPanel.add(new JLabel(entity.toString()), valueGbc);

        // Publisher
        labelGbc.gridy = valueGbc.gridy = row++;
        infoPanel.add(new JLabel("Publisher:"), labelGbc);
        String publisherText = (entity.getSeries() != null && entity.getSeries().getPublisher() != null)
                ? entity.getSeries().getPublisher().getName()
                : "None";
        JLabel publisherLabel = new JLabel(publisherText);
        publisherLabel.setForeground(Color.BLUE);
        publisherLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Publisher issuePublisher = (entity.getSeries() != null) ? entity.getSeries().getPublisher() : null;
        publisherLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (issuePublisher != null) {
                    detailsDialog.dispose();
                    Publisher fetchedPublisher = publisherService.getPublisherByIdWithSeriesAndCharacters(issuePublisher.getId());
                    new PublisherDetails(parent, fetchedPublisher, refreshCallback).showDetailsDialog();
                }
            }
        });
        infoPanel.add(publisherLabel, valueGbc);

        // Overview
        if (entity.getOverview() != null && !entity.getOverview().isBlank()) {
            labelGbc.gridy = valueGbc.gridy = row++;
            infoPanel.add(new JLabel("Overview:"), labelGbc);

            JTextArea overviewArea = new JTextArea(entity.getOverview());
            overviewArea.setLineWrap(true);
            overviewArea.setWrapStyleWord(true);
            overviewArea.setEditable(false);
            overviewArea.setBackground(infoPanel.getBackground());
            overviewArea.setBorder(null);
            overviewArea.setFont(UIManager.getFont("Label.font"));

            JScrollPane overviewScroll = new JScrollPane(overviewArea);
            overviewScroll.setPreferredSize(new Dimension(300, 60));
            overviewScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

            valueGbc.fill = GridBagConstraints.BOTH;
            valueGbc.weighty = 0.2;
            infoPanel.add(overviewScroll, valueGbc);
            valueGbc.fill = GridBagConstraints.HORIZONTAL; // reset for next
            valueGbc.weighty = 0;
        }

        // Release Date
        if (entity.getReleaseDate() != null) {
            labelGbc.gridy = valueGbc.gridy = row++;
            infoPanel.add(new JLabel("Release Date:"), labelGbc);
            infoPanel.add(new JLabel(entity.getReleaseDate().format(DateTimeFormatter.ISO_LOCAL_DATE)), valueGbc);
        }

        // Price
        if (entity.getPriceUsd() != null) {
            labelGbc.gridy = valueGbc.gridy = row++;
            infoPanel.add(new JLabel("Price (USD):"), labelGbc);
            infoPanel.add(new JLabel(String.format("$%.2f", entity.getPriceUsd())), valueGbc);
        }

        // Creators
        labelGbc.gridy = valueGbc.gridy = row++;
        infoPanel.add(new JLabel("Creators:"), labelGbc);
        String creatorsText = entity.getIssueCreators().stream()
                .map(ic -> ic.getCreator().getName() + " (" + ic.getRoles().stream().map(Enum::name).collect(Collectors.joining(", ")) + ")")
                .collect(Collectors.joining(", "));
        JTextArea creatorsArea = new JTextArea(creatorsText.isEmpty() ? "None" : creatorsText);
        creatorsArea.setLineWrap(true);
        creatorsArea.setWrapStyleWord(true);
        creatorsArea.setEditable(false);
        creatorsArea.setBackground(infoPanel.getBackground());
        creatorsArea.setBorder(null);
        creatorsArea.setFont(UIManager.getFont("Label.font"));
        JScrollPane creatorsScroll = new JScrollPane(creatorsArea);
        creatorsScroll.setPreferredSize(new Dimension(300, Math.min(60, creatorsText.split(", ").length * 20)));
        creatorsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        valueGbc.fill = GridBagConstraints.BOTH;
        valueGbc.weighty = 0.3;
        infoPanel.add(creatorsScroll, valueGbc);
        valueGbc.fill = GridBagConstraints.HORIZONTAL;
        valueGbc.weighty = 0;

        // Characters
        labelGbc.gridy = valueGbc.gridy = row++;
        infoPanel.add(new JLabel("Characters:"), labelGbc);
        String charactersText = entity.getCharacters().stream()
                .map(ComicCharacter::getName)
                .collect(Collectors.joining(", "));
        JTextArea charactersArea = new JTextArea(charactersText.isEmpty() ? "None" : charactersText);
        charactersArea.setLineWrap(true);
        charactersArea.setWrapStyleWord(true);
        charactersArea.setEditable(false);
        charactersArea.setBackground(infoPanel.getBackground());
        charactersArea.setBorder(null);
        charactersArea.setFont(UIManager.getFont("Label.font"));
        JScrollPane charactersScroll = new JScrollPane(charactersArea);
        charactersScroll.setPreferredSize(new Dimension(300, Math.min(60, charactersText.split(", ").length * 20)));
        charactersScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        valueGbc.fill = GridBagConstraints.BOTH;
        valueGbc.weighty = 0.3;
        infoPanel.add(charactersScroll, valueGbc);
        valueGbc.fill = GridBagConstraints.HORIZONTAL;
        valueGbc.weighty = 0;

        mainPanel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
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
        MainApp.showSuccess("Issue deleted.");
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Delete this issue?";
    }
}