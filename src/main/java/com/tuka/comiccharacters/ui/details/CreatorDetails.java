package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.ui.form.CreatorForm;

import javax.swing.*;
import java.awt.*;

public class CreatorDetails {

    public static void show(Component parent, Creator creator, Runnable refreshCallback) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Creator Details", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(parent);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setText("Name: " + creator.getName() +
                (creator.getOverview() != null && !creator.getOverview().isEmpty()
                        ? "\n\nOverview:\n" + creator.getOverview() : ""));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
        infoPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        editButton.addActionListener(e -> {
            dialog.dispose();
            showEdit(parent, creator, refreshCallback);
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(parent,
                    "Are you sure you want to delete " + creator.getName() + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new CreatorService().deleteCreator(creator.getId());
                dialog.dispose();
                refreshCallback.run();
            }
        });

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        dialog.add(infoPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public static void showEdit(Component parent, Creator creator, Runnable refreshCallback) {
        JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Edit Creator", true);
        CreatorForm editPanel = new CreatorForm(creator); // Should support edit constructor
        editDialog.setContentPane(editPanel);
        editDialog.pack();
        editDialog.setLocationRelativeTo(parent);
        editDialog.setVisible(true);
        refreshCallback.run();
    }
}
