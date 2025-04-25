package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.SeriesDaoImpl;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;

import java.util.ArrayList;
import java.util.List;

public class SeriesService extends AbstractService<Series> {

    public SeriesService() {
        super(new SeriesDaoImpl());
    }

    public void addSeries(String title, Integer startYear, Integer endYear, String overview, Publisher publisher) {
        Series series = new Series(title, startYear, endYear, overview, publisher);
        save(series);
    }

    public void updateSeries(Series updatedSeries) {
        save(updatedSeries);
    }

    public List<Issue> getIssuesBySeries(Series series) {
        if (series == null || series.getId() == null) {
            throw new IllegalArgumentException("Invalid series provided.");
        }
        Series loadedSeries = getByIdWithDetails(series.getId());
        return new ArrayList<>(loadedSeries.getIssues());
    }

    @Override
    protected void validateEntity(Series series) {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        if (series.getTitle() == null || series.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Series title cannot be empty");
        }

        // For existing series, validate the ID
        if (series.getId() != null && series.getId() <= 0) {
            throw new IllegalArgumentException("Invalid series ID");
        }

        // Start year should be numeric and reasonable
        if (series.getStartYear() != null) {
            int currentYear = java.time.Year.now().getValue();
            if (series.getStartYear() < 1000 || series.getStartYear() > currentYear + 5) {
                throw new IllegalArgumentException("Series start year must be between 1000 and " + (currentYear + 5));
            }
        }

        // End year should be after or equal to start year if both are provided
        if (series.getStartYear() != null && series.getEndYear() != null) {
            if (series.getEndYear() < series.getStartYear()) {
                throw new IllegalArgumentException("Series end year must be after or equal to start year");
            }
        }
    }
}
