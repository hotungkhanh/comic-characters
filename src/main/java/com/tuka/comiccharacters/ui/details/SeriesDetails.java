package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.SeriesService;
import com.tuka.comiccharacters.ui.MainApp;
import com.tuka.comiccharacters.ui.form.SeriesForm;

import javax.swing.*;
import java.awt.*;

public class SeriesDetails {

    public static void show(Series series, Runnable refreshCallback) {
        JDialog dialog = new JDialog((Frame) null, "Series Details", true);
        dialog.setLayout(new BorderLayout());

        String publisherText = series.getPublisher() != null ? series.getPublisher().toString() : "None";
        String message = series +
                "\nPublisher: " + publisherText +
                "\nIssues: " + series.getIssues().size();

        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        editBtn.addActionListener(e -> {
            dialog.dispose();
            showEdit(series, refreshCallback);
        });

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog, "Delete this series?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new SeriesService().deleteSeries(series.getId());
                MainApp.showSuccess("Series deleted.");
                dialog.dispose();
                refreshCallback.run();
            }
        });

        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(350, 220);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static void showEdit(Series series, Runnable refreshCallback) {
        JDialog dialog = new JDialog((Frame) null, "Edit Series", true);
        SeriesForm panel = new SeriesForm(series, refreshCallback, dialog);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
