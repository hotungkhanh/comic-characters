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
        String message = "Name: " + creator.getName();
        if (creator.getOverview() != null && !creator.getOverview().isEmpty()) {
            message += "\n\nOverview:\n" + creator.getOverview();
        }
        JOptionPane.showMessageDialog(this, message, "Creator Details", JOptionPane.INFORMATION_MESSAGE);
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
