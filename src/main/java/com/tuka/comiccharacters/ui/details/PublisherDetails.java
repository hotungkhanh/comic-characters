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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

public class PublisherDetails extends AbstractDetails<Publisher> {

    private final PublisherService publisherService = new PublisherService();
    private final SeriesService seriesService = new SeriesService();
    private final CharacterService characterService = new CharacterService();
    private JDialog detailsDialog;

    public PublisherDetails(Component parent, Publisher publisher, Runnable refreshCallback) {
        super(parent, publisher, refreshCallback);
    }

    public void showDetailsDialog() {
        super.showDetailsDialog(800, 500);
    }

    @Override
    protected JPanel getMainPanel(JDialog dialog) {
        this.detailsDialog = dialog;
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(500, 500));

        JPanel infoPanel = createMainInfoPanel();
        int row = 0;
        row = addLabelValue(infoPanel, "Name:", entity.getName(), row);

        List<Series> sortedSeries = entity.getPublisherSeries()
                .stream()
                .sorted(Comparator.comparing(Series::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();

        List<ComicCharacter> sortedCharacters = entity.getPublisherCharacters()
                .stream()
                .sorted(Comparator.comparing(ComicCharacter::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        JPanel seriesPanel = createInteractiveListPanel("Series", sortedSeries, Series::toString, s -> {
            Series fetched = seriesService.getByIdWithIssues(s.getId());
            if (fetched != null) {
                detailsDialog.dispose();
                new SeriesDetails(parent, fetched, refreshCallback).showDetailsDialog();
            } else {
                MainApp.showError("Could not load series details.");
            }
        });

        JPanel charactersPanel = createInteractiveListPanel("Characters", sortedCharacters, ComicCharacter::toString, c -> {
            ComicCharacter fetched = characterService.getCharacterByIdWithDetails(c.getId());
            if (fetched != null) {
                detailsDialog.dispose();
                new CharacterDetails(parent, fetched, refreshCallback).showDetailsDialog();
            } else {
                MainApp.showError("Could not load character details.");
            }
        });

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.add(seriesPanel);
        centerPanel.add(charactersPanel);

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private <T> JPanel createInteractiveListPanel(String title, List<T> items, java.util.function.Function<T, String> nameExtractor, java.util.function.Consumer<T> onDoubleClick) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        DefaultListModel<T> model = new DefaultListModel<>();
        items.forEach(model::addElement);
        JList<T> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    T selected = list.getSelectedValue();
                    if (selected != null) {
                        onDoubleClick.accept(selected);
                    }
                }
            }
        });
        list.setCellRenderer((JList<? extends T> l, T value, int index, boolean isSelected, boolean cellHasFocus) -> {
            JLabel label = new JLabel(nameExtractor.apply(value));
            label.setOpaque(true);
            if (isSelected) {
                label.setBackground(l.getSelectionBackground());
                label.setForeground(l.getSelectionForeground());
            } else {
                label.setBackground(l.getBackground());
                label.setForeground(l.getForeground());
            }
            return label;
        });
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
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
