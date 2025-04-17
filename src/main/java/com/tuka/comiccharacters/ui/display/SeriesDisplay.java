package com.tuka.comiccharacters.ui.display;

import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.SeriesService;
import com.tuka.comiccharacters.ui.MainApp;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SeriesDisplay extends JPanel {
    private final DefaultListModel<Series> seriesListModel;
    private final JList<Series> seriesJList;
    private final SeriesService seriesService;
    private final List<Series> allSeries; // Full list for filtering

    public SeriesDisplay() {
        this.seriesService = new SeriesService();
        this.seriesListModel = new DefaultListModel<>();
        this.seriesJList = new JList<>(seriesListModel);
        this.allSeries = seriesService.getAllSeries(); // Get all series initially
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
                    MainApp.showSeriesPopup(fullSeries, this::refreshSeries);
                }
            }
        });

        refreshSeries();
    }

    public void filter(String query) {
        seriesListModel.clear();

        for (Series s : allSeries) {
            if (s.getTitle().toLowerCase().contains(query.toLowerCase())) {
                seriesListModel.addElement(s);
            }
        }
    }

    public void refreshSeries() {
        seriesListModel.clear();
        for (Series s : allSeries) {
            seriesListModel.addElement(s);
        }
    }
}
