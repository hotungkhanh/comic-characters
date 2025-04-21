package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.SeriesService;
import com.tuka.comiccharacters.ui.MainApp;
import com.tuka.comiccharacters.ui.form.SeriesForm;

import javax.swing.*;
import java.awt.*;

public class SeriesDetails extends AbstractDetails<Series> {

    public SeriesDetails(Component parent, Series series, Runnable refreshCallback) {
        super(parent, series, refreshCallback);
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        String publisherText = entity.getPublisher() != null ? entity.getPublisher().toString() : "None";
        String message = entity +
                "\nPublisher: " + publisherText +
                "\nIssues: " + entity.getIssues().size();

        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
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
        return "Delete this series?";
    }
}
