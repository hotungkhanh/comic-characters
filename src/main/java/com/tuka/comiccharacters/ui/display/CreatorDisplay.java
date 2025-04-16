package com.tuka.comiccharacters.ui.display;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.ui.panel.CreatorPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class CreatorDisplay extends JPanel {

    private final CreatorService creatorService;
    private final DefaultListModel<Creator> listModel;
    private final List<Creator> allCreators; // Full list for filtering

    public CreatorDisplay() {
        this.creatorService = new CreatorService();
        this.listModel = new DefaultListModel<>();
        this.allCreators = new ArrayList<>();

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Creators"));

        // Search bar
        JTextField searchField = new JTextField();
        searchField.setToolTipText("Search creators...");
        add(searchField, BorderLayout.NORTH);

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

        // Add Creator button
        JButton addButton = new JButton("Add Creator");
        addButton.addActionListener(e -> {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Creator", true);
            dialog.setContentPane(new CreatorPanel());
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            refreshCreators();
        });
        add(addButton, BorderLayout.SOUTH);

        // Filter logic on typing
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                String query = searchField.getText().trim().toLowerCase();
                listModel.clear();
                for (Creator creator : allCreators) {
                    if (creator.getName().toLowerCase().contains(query)) {
                        listModel.addElement(creator);
                    }
                }
            }

            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
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
            CreatorPanel editPanel = new CreatorPanel(creator); // Assume you overload CreatorPanel for editing
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
}
