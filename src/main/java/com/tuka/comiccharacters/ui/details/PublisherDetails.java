package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.ui.MainApp;
import com.tuka.comiccharacters.ui.form.PublisherForm;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class PublisherDetails extends AbstractDetails<Publisher> {

    private final PublisherService publisherService = new PublisherService();

    public PublisherDetails(Component parent, Publisher publisher, Runnable refreshCallback) {
        super(parent, publisher, refreshCallback);
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(500, 500));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(new JLabel(entity.getName()));

        List<Series> sortedSeries = entity.getPublisherSeries()
                .stream()
                .sorted(Comparator.comparing(Series::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();

        List<ComicCharacter> sortedCharacters = entity.getPublisherCharacters()
                .stream()
                .sorted(Comparator.comparing(ComicCharacter::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel seriesPanel = new JPanel(new BorderLayout());
        seriesPanel.setBorder(BorderFactory.createTitledBorder("Series"));
        JList<Series> seriesList = new JList<>(sortedSeries.toArray(new Series[0]));
        seriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        seriesPanel.add(new JScrollPane(seriesList), BorderLayout.CENTER);

        JPanel charactersPanel = new JPanel(new BorderLayout());
        charactersPanel.setBorder(BorderFactory.createTitledBorder("Characters"));
        JList<ComicCharacter> characterList = new JList<>(sortedCharacters.toArray(new ComicCharacter[0]));
        characterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        charactersPanel.add(new JScrollPane(characterList), BorderLayout.CENTER);

        centerPanel.add(seriesPanel);
        centerPanel.add(charactersPanel);

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    @Override
    protected String getTitle() {
        return "Publisher Details";
    }

    @Override
    protected void showEditDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Edit Publisher", true);
        PublisherForm panel = new PublisherForm(entity);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        refreshCallback.run();
    }

    @Override
    protected void deleteEntity() {
        publisherService.deletePublisher(entity.getId());
        MainApp.showSuccess("Publisher deleted.");
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Delete this publisher and all associated series and characters?";
    }
}
