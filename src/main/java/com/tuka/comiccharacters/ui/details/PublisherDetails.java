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
        // Create the main panel with a scrollable view
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

        // Create the basic info panel
        JPanel infoPanel = createMainInfoPanel();
        int row = 0;

        // Basic info
        row = addLabelValue(infoPanel, "Name:", entity.getName(), row);

        contentPanel.add(infoPanel, BorderLayout.NORTH);

        // Get sorted collections
        List<Series> sortedSeries = entity.getPublisherSeries().stream().sorted(Comparator.comparing(Series::getTitle, String.CASE_INSENSITIVE_ORDER)).toList();

        List<ComicCharacter> sortedCharacters = entity.getPublisherCharacters().stream().sorted(Comparator.comparing(ComicCharacter::getName, String.CASE_INSENSITIVE_ORDER)).toList();

        // Create the side-by-side panels for series and characters
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Only add panels if they have content
        if (!sortedSeries.isEmpty()) {
            JPanel seriesPanel = createClickableListPanel("Series", sortedSeries, Series::getTitle, this::navigateToSeries);
            listsPanel.add(seriesPanel);
        }

        if (!sortedCharacters.isEmpty()) {
            JPanel charactersPanel = createClickableListPanel("Characters", sortedCharacters, ComicCharacter::getName, this::navigateToCharacter);
            listsPanel.add(charactersPanel);
        }

        contentPanel.add(listsPanel, BorderLayout.CENTER);
        mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);

        return mainPanel;
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
        navigateToCharacter(character, characterService);
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
