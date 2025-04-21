package com.tuka.comiccharacters.ui.details;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractDetails<T> {

    protected final Component parent;
    protected final T entity;
    protected final Runnable refreshCallback;

    public AbstractDetails(Component parent, T entity, Runnable refreshCallback) {
        this.parent = parent;
        this.entity = entity;
        this.refreshCallback = refreshCallback;
    }

    public void showDetailsDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), getTitle(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(parent);

        dialog.add(getMainPanel(dialog), BorderLayout.CENTER);
        dialog.add(getButtonPanel(dialog), BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    protected JPanel getButtonPanel(JDialog dialog) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        editButton.addActionListener(_ -> {
            dialog.dispose();
            showEditDialog();
        });

        deleteButton.addActionListener(_ -> {
            int confirm = JOptionPane.showConfirmDialog(parent, getDeleteConfirmationMessage(), "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteEntity();
                dialog.dispose();
                refreshCallback.run();
            }
        });

        panel.add(editButton);
        panel.add(deleteButton);
        return panel;
    }

    protected abstract JPanel getMainPanel(JDialog dialog);

    protected abstract String getTitle();

    protected abstract void showEditDialog();

    protected abstract void deleteEntity();

    protected abstract String getDeleteConfirmationMessage();
}
