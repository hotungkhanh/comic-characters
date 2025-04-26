package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.IssueService;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.service.SeriesService;
import com.tuka.comiccharacters.ui.MainApp;
import com.tuka.comiccharacters.ui.form.IssueForm;
import com.tuka.comiccharacters.ui.form.SeriesForm;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class SeriesDetails extends AbstractDetails<Series> {

    private final PublisherService publisherService;
    private final IssueService issueService;
    private final SeriesService seriesService;
    private JList<Issue> issueList;

    public SeriesDetails(Component parent, Series series, Runnable refreshCallback) {
        super(parent, series, refreshCallback);
        this.publisherService = new PublisherService();
        this.issueService = new IssueService();
        this.seriesService = new SeriesService();
    }

    @Override
    public void showDetailsDialog() {
        super.showDetailsDialog(600, 750, "Series");
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        JPanel infoPanel = createMainInfoPanel();
        int row = 0;

        // Basic series information
        row = addLabelValue(infoPanel, "Title:", entity.getTitle(), row);

        // Year range
        String yearRange = (entity.getEndYear() != null) ? entity.getStartYear() + " - " + entity.getEndYear() : entity.getStartYear() + " - Present";
        row = addLabelValue(infoPanel, "Years Published:", yearRange, row);

        // Publisher link
        row = addClickablePublisher(infoPanel, row, entity.getPublisher(), publisherService);

        // Overview
        row = addTextArea(infoPanel, "Overview:", entity.getOverview(), row, 6);

        // Issues panel with add button
        JPanel issuesPanel = createIssuesPanel();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        infoPanel.add(issuesPanel, gbc);

        // Main scrollable panel (original layout)
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createIssuesPanel() {
        JPanel issuesPanel = new JPanel(new BorderLayout());
        issuesPanel.setBorder(BorderFactory.createTitledBorder("Issues"));

        // Create and populate the issues list
        List<Issue> sortedIssues = entity.getIssues().stream().sorted(Comparator.comparing(Issue::toString)).toList();

        // Create list model and populate it
        DefaultListModel<Issue> issueListModel = new DefaultListModel<>();
        sortedIssues.forEach(issueListModel::addElement);

        // Create the list with selection capabilities
        issueList = new JList<>(issueListModel);
        issueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add double-click listener for navigation
        issueList.addMouseListener(getListDoubleClickListener(sortedIssues, this::navigateToIssue));

        JScrollPane issueScrollPane = new JScrollPane(issueList);
        issuesPanel.add(issueScrollPane, BorderLayout.CENTER);

        // Add the "Add New Issues" button
        JButton addIssueButton = new JButton("Add New Issues");
        addIssueButton.addActionListener(_ -> {
            JDialog addIssueDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Add New Issues", true);
            IssueForm issueForm = new IssueForm(entity, this::refreshDetails);
            addIssueDialog.setContentPane(issueForm);
            addIssueDialog.pack();
            addIssueDialog.setLocationRelativeTo(parent);
            addIssueDialog.setVisible(true);
        });

        issuesPanel.add(addIssueButton, BorderLayout.SOUTH);

        return issuesPanel;
    }

    private void navigateToIssue(Issue issue) {
        Issue fetched = issueService.getByIdWithDetails(issue.getId());
        if (fetched != null) {
            currentDialog.dispose();
            new IssueDetails(parent, fetched, this::refreshDetails).showDetailsDialog();
        } else {
            MainApp.showError("Could not load issue details.");
        }
    }

    private void refreshDetails() {
        Series updatedSeries = seriesService.getByIdWithDetails(entity.getId());
        entity.setIssues(updatedSeries.getIssues());

        SwingUtilities.invokeLater(() -> {
            DefaultListModel<Issue> issueListModel = (DefaultListModel<Issue>) issueList.getModel();
            issueListModel.clear();

            entity.getIssues().stream().sorted(Comparator.comparing(Issue::toString)).forEach(issueListModel::addElement);

            issueList.revalidate();
            issueList.repaint();
            currentDialog.revalidate();
            currentDialog.repaint();
        });
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
        seriesService.delete(entity.getId());
        MainApp.showSuccess("Series deleted.");
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Delete this series and all its issues?";
    }
}
