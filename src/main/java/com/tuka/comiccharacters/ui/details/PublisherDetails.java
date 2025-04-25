package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.service.SeriesService;
import com.tuka.comiccharacters.ui.MainApp;
import com.tuka.comiccharacters.ui.form.PublisherForm;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class PublisherDetails extends AbstractDetails<Publisher> {

    private final PublisherService publisherService;
    private final SeriesService seriesService;
    private final CharacterService characterService;

    public PublisherDetails(Component parent, Publisher publisher, Runnable refreshCallback) {
        super(parent, publisher, refreshCallback);
        this.publisherService = new PublisherService();
        this.seriesService = new SeriesService();
        this.characterService = new CharacterService();
    }

    @Override
    public void showDetailsDialog() {
        super.showDetailsDialog(900, 500);
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Create the basic info panel
        JPanel infoPanel = createMainInfoPanel();
        int row = 0;
        row = addLabelValue(infoPanel, "Name:", entity.getName(), row);

        // Get sorted collections
        List<Series> sortedSeries = getSortedSeries();
        List<ComicCharacter> sortedCharacters = getSortedCharacters();

        // Create the side-by-side panels for series and characters
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        if (!sortedSeries.isEmpty()) {
            JPanel seriesPanel = createClickableListPanel("Series", sortedSeries, Series::toString, this::navigateToSeries);
            centerPanel.add(seriesPanel);
        }

        if (!sortedCharacters.isEmpty()) {
            JPanel charactersPanel = createClickableListPanel("Characters", sortedCharacters, ComicCharacter::toString, this::navigateToCharacter);
            centerPanel.add(charactersPanel);
        }

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private List<Series> getSortedSeries() {
        return entity.getPublisherSeries().stream().sorted(Comparator.comparing(Series::getTitle, String.CASE_INSENSITIVE_ORDER)).toList();
    }

    private List<ComicCharacter> getSortedCharacters() {
        return entity.getPublisherCharacters().stream().sorted(Comparator.comparing(ComicCharacter::getName, String.CASE_INSENSITIVE_ORDER)).toList();
    }

    private void navigateToSeries(Series series) {
        Series fetched = seriesService.getByIdWithDetails(series.getId());
        if (fetched != null) {
            currentDialog.dispose();
            new SeriesDetails(parent, fetched, refreshCallback).showDetailsDialog();
        } else {
            MainApp.showError("Could not load series details.");
        }
    }

    private void navigateToCharacter(ComicCharacter character) {
        ComicCharacter fetched = characterService.getByIdWithDetails(character.getId());
        if (fetched != null) {
            currentDialog.dispose();
            new CharacterDetails(parent, fetched, refreshCallback).showDetailsDialog();
        } else {
            MainApp.showError("Could not load character details.");
        }
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
        publisherService.delete(entity.getId());
        MainApp.showSuccess("Publisher deleted.");
    }

    @Override
    protected String getDeleteConfirmationMessage() {
        return "Delete this publisher? Associated series and characters will not be deleted.";
    }
}
