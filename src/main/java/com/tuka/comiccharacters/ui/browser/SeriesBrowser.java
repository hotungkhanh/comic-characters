package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.SeriesService;
import com.tuka.comiccharacters.ui.details.AbstractDetails;
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
        return matchesNameField(series.getTitle(), query);
    }

    @Override
    protected Comparator<Series> getComparator() {
        return Comparator.comparing(s -> s.getTitle().toLowerCase());
    }

    @Override
    protected Long getEntityId(Series series) {
        return series.getId();
    }

    @Override
    protected JComponent createForm() {
        return new SeriesForm();
    }

    @Override
    protected AbstractDetails<Series> createDetailsDialog(Series series, Runnable refreshCallback) {
        return new SeriesDetails(this, series, refreshCallback);
    }
}
