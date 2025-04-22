package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.IssueService;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.service.SeriesService;
import com.tuka.comiccharacters.ui.MainApp;
import com.tuka.comiccharacters.ui.form.IssueForm;
import com.tuka.comiccharacters.ui.form.SeriesForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

public class SeriesDetails extends AbstractDetails<Series> {

    private final PublisherService publisherService = new PublisherService();
    private final IssueService issueService = new IssueService(); // Add IssueService
    private JDialog detailsDialog;
    private JList<Issue> issueList;

    public SeriesDetails(Component parent, Series series, Runnable refreshCallback) {
        super(parent, series, refreshCallback);
    }

    @Override
    public void showDetailsDialog() {
        detailsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), getTitle(), true);
        detailsDialog.setLayout(new BorderLayout(10, 10));
        detailsDialog.setSize(400, 300); // Increased size to accommodate potential scrollbar
        detailsDialog.setLocationRelativeTo(parent);

        detailsDialog.add(getMainPanel(detailsDialog), BorderLayout.CENTER); // Pass the dialog here
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

        // Title
        labelGbc.gridy = valueGbc.gridy = row++;
        infoPanel.add(new JLabel("Title:"), labelGbc);
        infoPanel.add(new JLabel(entity.getTitle()), valueGbc);

        // Years Published
        String yearRange = (entity.getEndYear() != null)
                ? entity.getStartYear() + " - " + entity.getEndYear()
                : entity.getStartYear() + " - Present";
        labelGbc.gridy = valueGbc.gridy = row++;
        infoPanel.add(new JLabel("Years Published:"), labelGbc);
        infoPanel.add(new JLabel(yearRange), valueGbc);

        // Publisher
        labelGbc.gridy = valueGbc.gridy = row++;
        infoPanel.add(new JLabel("Publisher:"), labelGbc);
        String publisherText = (entity.getPublisher() != null) ? entity.getPublisher().getName() : "None";
        JLabel publisherLabel = new JLabel(publisherText);
        publisherLabel.setForeground(Color.BLUE);
        publisherLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Publisher seriesPublisher = entity.getPublisher();
        publisherLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (seriesPublisher != null) {
                    detailsDialog.dispose();
                    Publisher fetchedPublisher = publisherService.getPublisherByIdWithSeriesAndCharacters(seriesPublisher.getId());
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
            overviewScroll.setPreferredSize(new Dimension(300, 100));
            overviewScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

            valueGbc.fill = GridBagConstraints.BOTH;
            valueGbc.weighty = 0.3;
            infoPanel.add(overviewScroll, valueGbc);
            valueGbc.fill = GridBagConstraints.HORIZONTAL; // reset for next
            valueGbc.weighty = 0;
        }

        // Issues
        JPanel issuesPanel = new JPanel(new BorderLayout());
        issuesPanel.setBorder(BorderFactory.createTitledBorder("Issues"));

        List<Issue> sortedIssues = entity.getIssues().stream()
                .sorted(Comparator.comparing(Issue::toString))
                .toList();
        DefaultListModel<Issue> issueListModel = new DefaultListModel<>();
        sortedIssues.forEach(issueListModel::addElement);

        issueList = new JList<>(issueListModel);
        issueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane issueScrollPane = new JScrollPane(issueList);
        issuesPanel.add(issueScrollPane, BorderLayout.CENTER);

        // Add MouseListener to the issueList
        issueList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double-click to open details
                    Issue selectedIssue = issueList.getSelectedValue();
                    if (selectedIssue != null) {
                        Issue fetchedIssue = issueService.getIssueByIdWithDetails(selectedIssue.getId());
                        if (fetchedIssue != null) {
                            new IssueDetails(parent, fetchedIssue, SeriesDetails.this::refreshDetails).showDetailsDialog();
                        } else {
                            MainApp.showError("Could not load issue details.");
                        }
                    }
                }
            }
        });

        JButton addIssueButton = getAddIssuesButton(dialog, mainPanel);
        issuesPanel.add(addIssueButton, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        infoPanel.add(issuesPanel, gbc);

        mainPanel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        return mainPanel;
    }

    private void refreshDetails() {
        SeriesService seriesService = new SeriesService();
        Series updatedSeries = seriesService.getByIdWithIssues(entity.getId());
        SeriesDetails.this.entity.setIssues(updatedSeries.getIssues());
        SwingUtilities.invokeLater(() -> {
            DefaultListModel<Issue> issueListModel = (DefaultListModel<Issue>) issueList.getModel();
            issueListModel.clear();
            updatedSeries.getIssues().stream()
                    .sorted(Comparator.comparing(Issue::toString))
                    .forEach(issueListModel::addElement);
            issueList.revalidate();
            issueList.repaint();
            detailsDialog.revalidate();
            detailsDialog.repaint();
        });
    }

    private JButton getAddIssuesButton(JDialog dialog, JPanel mainPanel) {
        JButton addIssuesButton = new JButton("Add New Issues");
        addIssuesButton.addActionListener(_ -> {
            JDialog addIssueDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Add New Issues", true);
            IssueForm issueForm = new IssueForm(entity, this::refreshDetails, addIssueDialog);
            addIssueDialog.setContentPane(issueForm);
            addIssueDialog.pack();
            addIssueDialog.setLocationRelativeTo(parent);
            addIssueDialog.setVisible(true);
        });
        return addIssuesButton;
    }

    @Override
    protected String getTitle() {
        return "Series Details";
    }

    @Override
    protected void showEditDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Edit Series", true);
        SeriesForm panel = new SeriesForm(entity, refreshCallback, dialog);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    @Override
    protected void deleteEntity() {
        new SeriesService().deleteSeries(entity.getId());
        MainApp.showSuccess("Series deleted.");
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Delete this series and all its issues?";
    }
}