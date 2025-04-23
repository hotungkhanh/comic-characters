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

    public void showDetailsDialog() {
        super.showDetailsDialog(600, 700);
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        this.detailsDialog = dialog;
        JPanel infoPanel = createMainInfoPanel();
        int row = 0;

        row = addLabelValue(infoPanel, "Title:", entity.getTitle(), row);

        String yearRange = (entity.getEndYear() != null)
                ? entity.getStartYear() + " - " + entity.getEndYear()
                : entity.getStartYear() + " - Present";
        row = addLabelValue(infoPanel, "Years Published:", yearRange, row);

        row = addPublisherLink(infoPanel, row);

        row = addTextArea(infoPanel, "Overview:", entity.getOverview(), row, 6);

        JPanel issuesPanel = createIssuesPanel();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        infoPanel.add(issuesPanel, gbc);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        return mainPanel;
    }

    private int addPublisherLink(JPanel panel, int row) {
        Publisher seriesPublisher = entity.getPublisher();
        MouseAdapter mouseAdapter = null;
        if (seriesPublisher != null) {
            mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    detailsDialog.dispose();
                    Publisher fetchedPublisher = publisherService.getPublisherByIdWithSeriesAndCharacters(seriesPublisher.getId());
                    new PublisherDetails(parent, fetchedPublisher, refreshCallback).showDetailsDialog();
                }
            };
        }
        String publisherText = (entity.getPublisher() != null) ? entity.getPublisher().getName() : "None";
        return addClickableLabel(panel, "Publisher:", publisherText, row, mouseAdapter, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JPanel createIssuesPanel() {
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

        issueList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
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

        JButton addIssueButton = getAddIssuesButton();
        issuesPanel.add(addIssueButton, BorderLayout.SOUTH);
        return issuesPanel;
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

    private JButton getAddIssuesButton() {
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
