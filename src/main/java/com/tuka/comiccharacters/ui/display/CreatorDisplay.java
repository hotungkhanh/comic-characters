package com.tuka.comiccharacters.ui.display;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.ui.panel.CreatorPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CreatorDisplay extends JPanel {

    private final CreatorService creatorService;
    private final DefaultListModel<Creator> listModel;

    public CreatorDisplay() {
        this.creatorService = new CreatorService();
        this.listModel = new DefaultListModel<>();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Creators"));

        JList<Creator> creatorList = new JList<>(listModel);
        creatorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(creatorList);
        add(scrollPane, BorderLayout.CENTER);

        // Double-click to view more details
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

        JButton addButton = new JButton("Add Creator");
        addButton.addActionListener(e -> {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Creator", true);
            dialog.setContentPane(new CreatorPanel()); // No callback
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            // Optional: Refresh after dialog closes
            refreshCreators();
        });

        add(addButton, BorderLayout.SOUTH);
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
        List<Creator> creators = creatorService.getAllCreators();
        creators.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        for (Creator creator : creators) {
            listModel.addElement(creator);
        }
    }
}
