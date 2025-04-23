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

    public void showDetailsDialog() {
        super.showDetailsDialog(800, 500);
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(500, 500));

        // Info Panel for Publisher name
        JPanel infoPanel = createMainInfoPanel();
        int row = 0;
        row = addLabelValue(infoPanel, "Name:", entity.getName(), row);

        // Sorted Lists
        List<Series> sortedSeries = entity.getPublisherSeries()
                .stream()
                .sorted(Comparator.comparing(Series::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();

        List<ComicCharacter> sortedCharacters = entity.getPublisherCharacters()
                .stream()
                .sorted(Comparator.comparing(ComicCharacter::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        // Use reusable method to create panels
        JPanel seriesPanel = createListPanel("Series", sortedSeries, Series::getTitle, 200);
        JPanel charactersPanel = createListPanel("Characters", sortedCharacters, ComicCharacter::getName, 200);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
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
        return "Delete this publisher? Associated series and characters will not be deleted.";
    }
}
