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

    public SeriesDisplay() {
        this.seriesService = new SeriesService();
        this.seriesListModel = new DefaultListModel<>();
        this.seriesJList = new JList<>(seriesListModel);
        this.seriesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.seriesJList.setFont(new Font("SansSerif", Font.PLAIN, 14));

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Series"));

        JScrollPane scrollPane = new JScrollPane(seriesJList);
        add(scrollPane, BorderLayout.CENTER);

        seriesJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Series selected = seriesJList.getSelectedValue();
                if (selected != null) {
                    Series fullSeries = seriesService.getByIdWithIssues(selected.getId());
                    MainApp.showSeriesPopup(fullSeries);
                }
            }
        });


        refreshSeries();
    }

    public void refreshSeries() {
        seriesListModel.clear();
        List<Series> allSeries = seriesService.getAllSeries();
        for (Series s : allSeries) {
            seriesListModel.addElement(s);
        }
    }
}
