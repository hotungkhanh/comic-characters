package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.SeriesService;
import com.tuka.comiccharacters.ui.details.SeriesDetails;
import com.tuka.comiccharacters.ui.form.SeriesForm;

import javax.swing.*;
import java.util.Comparator;

public class SeriesBrowser extends AbstractBrowser<Series, SeriesService> {

    public SeriesBrowser(JFrame parentFrame) {
        super("Series", parentFrame, new SeriesService());
    }

    @Override
    protected boolean matchesQuery(Series series, String query) {
        return series.getTitle().toLowerCase().contains(query.toLowerCase());
    }

    @Override
    protected Comparator<Series> getComparator() {
        return Comparator.comparing(s -> s.getTitle().toLowerCase());
    }

    @Override
    protected void showDetails(Series series) {
        Series fullSeries = service.getByIdWithDetails(series.getId());
        new SeriesDetails(this, fullSeries, this::refreshEntities).showDetailsDialog();
    }

    @Override
    protected void showAddForm() {
        JDialog dialog = new JDialog(parentFrame, "Add New Series", true);
        dialog.setContentPane(new SeriesForm());
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
        refreshEntities();
    }
}
