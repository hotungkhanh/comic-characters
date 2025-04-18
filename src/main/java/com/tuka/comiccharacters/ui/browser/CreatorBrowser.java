package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.ui.form.CreatorForm;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CreatorBrowser extends JPanel {

    private final CreatorService creatorService;
    private final DefaultListModel<Creator> listModel;
    private final List<Creator> allCreators; // Full list for filtering

    public CreatorBrowser() {
        this.creatorService = new CreatorService();
        this.listModel = new DefaultListModel<>();
        this.allCreators = new ArrayList<>();

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Creators"));

        // Creator list
        JList<Creator> creatorList = new JList<>(listModel);
        creatorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(creatorList);
        add(scrollPane, BorderLayout.CENTER);

        // Double-click to view details
        creatorList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    Creator selected = creatorList.getSelectedValue();
                    if (selected != null) {
                        showCreatorDetails(selected);
                    }
                }
            }
        });

        refreshCreators();
    }

    private void showCreatorDetails(Creator creator) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Creator Details", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setText("Name: " + creator.getName() +
                (creator.getOverview() != null && !creator.getOverview().isEmpty()
                        ? "\n\nOverview:\n" + creator.getOverview() : ""));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        infoPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        // Edit button behavior
        editButton.addActionListener(e -> {
            dialog.dispose();

            // Show a new dialog pre-filled with this creator
            JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Creator", true);
            CreatorForm editPanel = new CreatorForm(creator); // Assume you overload CreatorPanel for editing
            editDialog.setContentPane(editPanel);
            editDialog.pack();
            editDialog.setLocationRelativeTo(this);
            editDialog.setVisible(true);

            refreshCreators();
        });

        // Delete button behavior
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete " + creator.getName() + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                creatorService.deleteCreator(creator.getId());
                refreshCreators();
                dialog.dispose();
            }
        });

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        dialog.add(infoPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void refreshCreators() {
        listModel.clear();
        allCreators.clear();

        List<Creator> creators = creatorService.getAllCreators();
        creators.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        allCreators.addAll(creators);

        for (Creator creator : creators) {
            listModel.addElement(creator);
        }
    }

    public void filter(String query) {
        listModel.clear();
        for (Creator creator : allCreators) {
            if (creator.getName().toLowerCase().contains(query.toLowerCase())) {
                listModel.addElement(creator);
            }
        }
    }
}

