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
        super.showDetailsDialog(600, 750);
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        JPanel infoPanel = createMainInfoPanel();
        int row = 0;

        // Basic series information
        row = addLabelValue(infoPanel, "Title:", entity.getTitle(), row);

        // Year range
        String yearRange = formatYearRange();
        row = addLabelValue(infoPanel, "Years Published:", yearRange, row);

        // Publisher link
        row = addPublisherLink(infoPanel, row);

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

        // Main scrollable panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        return mainPanel;
    }

    private String formatYearRange() {
        return (entity.getEndYear() != null) ? entity.getStartYear() + " - " + entity.getEndYear() : entity.getStartYear() + " - Present";
    }

    private int addPublisherLink(JPanel panel, int row) {
        Publisher publisher = entity.getPublisher();
        if (publisher == null) {
            return row;
        }

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToPublisher(publisher);
            }
        };

        return addClickableLabel(panel, "Publisher:", publisher.getName(), row, mouseAdapter, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void navigateToPublisher(Publisher publisher) {
        Publisher fetched = publisherService.getByIdWithDetails(publisher.getId());
        if (fetched != null) {
            currentDialog.dispose();
            new PublisherDetails(parent, fetched, refreshCallback).showDetailsDialog();
        } else {
            MainApp.showError("Could not load publisher details.");
        }
    }

    private JPanel createIssuesPanel() {
        JPanel issuesPanel = new JPanel(new BorderLayout());
        issuesPanel.setBorder(BorderFactory.createTitledBorder("Issues"));

        // Create and populate the issues list
        List<Issue> sortedIssues = getSortedIssues();
        issueList = createIssuesList(sortedIssues);

        JScrollPane issueScrollPane = new JScrollPane(issueList);
        issuesPanel.add(issueScrollPane, BorderLayout.CENTER);

        // Add the "Add New Issues" button
        JButton addIssueButton = createAddIssuesButton();
        issuesPanel.add(addIssueButton, BorderLayout.SOUTH);

        return issuesPanel;
    }

    private List<Issue> getSortedIssues() {
        return entity.getIssues().stream().sorted(Comparator.comparing(Issue::toString)).toList();
    }

    private JList<Issue> createIssuesList(List<Issue> issues) {
        DefaultListModel<Issue> issueListModel = new DefaultListModel<>();
        issues.forEach(issueListModel::addElement);

        JList<Issue> list = new JList<>(issueListModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    navigateToSelectedIssue();
                }
            }
        });

        return list;
    }

    private void navigateToSelectedIssue() {
        Issue selectedIssue = issueList.getSelectedValue();
        if (selectedIssue != null) {
            Issue fetched = issueService.getByIdWithDetails(selectedIssue.getId());
            if (fetched != null) {
                currentDialog.dispose();
                new IssueDetails(parent, fetched, this::refreshDetails).showDetailsDialog();
            } else {
                MainApp.showError("Could not load issue details.");
            }
        }
    }

    private JButton createAddIssuesButton() {
        JButton addIssuesButton = new JButton("Add New Issues");
        addIssuesButton.addActionListener(_ -> {
            JDialog addIssueDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Add New Issues", true);

            IssueForm issueForm = new IssueForm(entity, this::refreshDetails, addIssueDialog);
            addIssueDialog.setContentPane(issueForm);
            addIssueDialog.pack();
            addIssueDialog.setSize(700, 900);
            addIssueDialog.setLocationRelativeTo(parent);
            addIssueDialog.setVisible(true);
        });

        return addIssuesButton;
    }

    private void refreshDetails() {
        Series updatedSeries = seriesService.getByIdWithDetails(entity.getId());
        entity.setIssues(updatedSeries.getIssues());

        SwingUtilities.invokeLater(() -> {
            DefaultListModel<Issue> issueListModel = (DefaultListModel<Issue>) issueList.getModel();
            issueListModel.clear();

            getSortedIssues().forEach(issueListModel::addElement);

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
