package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.SeriesService;
import com.tuka.comiccharacters.ui.details.SeriesDetails;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class SeriesBrowser extends JPanel {
    private final DefaultListModel<Series> seriesListModel;
    private final JList<Series> seriesJList;
    private final SeriesService seriesService;
    private List<Series> allSeries;

    public SeriesBrowser() {
        this.seriesService = new SeriesService();
        this.seriesListModel = new DefaultListModel<>();
        this.seriesJList = new JList<>(seriesListModel);
        this.seriesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Series"));

        JScrollPane scrollPane = new JScrollPane(seriesJList);
        add(scrollPane, BorderLayout.CENTER);

        // Double-click to view series details
        seriesJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Series selected = seriesJList.getSelectedValue();
                if (selected != null) {
                    Series fullSeries = seriesService.getByIdWithIssues(selected.getId());
                    SeriesDetails.show(fullSeries, this::refreshSeries);
                }
            }
        });

        refreshSeries();
    }

    public void filter(String query) {
        seriesListModel.clear();
        if (allSeries == null) return;

        List<Series> filtered = allSeries.stream()
                .filter(s -> s.getTitle().toLowerCase().contains(query.toLowerCase()))
                .toList();

        for (Series s : filtered) {
            seriesListModel.addElement(s);
        }
    }

    public void refreshSeries() {
        seriesListModel.clear();
        allSeries = seriesService.getAllSeries().stream()
                .sorted((s1, s2) -> s1.getTitle().compareToIgnoreCase(s2.getTitle()))
                .collect(Collectors.toList());

        for (Series s : allSeries) {
            seriesListModel.addElement(s);
        }
    }
}
