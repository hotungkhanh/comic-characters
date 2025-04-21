package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.ui.form.CreatorForm;

import javax.swing.*;
import java.awt.*;

public class CreatorDetails extends AbstractDetails<Creator> {

    public CreatorDetails(Component parent, Creator creator, Runnable refreshCallback) {
        super(parent, creator, refreshCallback);
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setText("Name: " + entity.getName() +
                (entity.getOverview() != null && !entity.getOverview().isEmpty()
                        ? "\n\nOverview:\n" + entity.getOverview() : ""));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

    @Override
    protected String getTitle() {
        return "Creator Details";
    }

    @Override
    protected void showEditDialog() {
        JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Edit Creator", true);
        CreatorForm editPanel = new CreatorForm(entity);
        editDialog.setContentPane(editPanel);
        editDialog.pack();
        editDialog.setLocationRelativeTo(parent);
        editDialog.setVisible(true);
        refreshCallback.run();
    }

    @Override
    protected void deleteEntity() {
        new CreatorService().deleteCreator(entity.getId());
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Are you sure you want to delete " + entity.getName() + "?";
    }
}

