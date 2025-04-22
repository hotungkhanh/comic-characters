package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.SeriesDaoImpl;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;

import java.util.List;
import java.util.Set;

public class SeriesService {
    private final SeriesDaoImpl seriesDao = new SeriesDaoImpl();

    public void addSeries(String title, Integer startYear, Integer endYear, String overview, Publisher publisher) {
        Series series = new Series(title, startYear, endYear, overview, publisher);
        seriesDao.save(series);
    }

    public Set<Series> getAllSeries() {
        return seriesDao.findAll();
    }

    public Series getByIdWithIssues(Long id) {
        return seriesDao.findByIdWithDetails(id);
    }

    public void updateSeries(Series updatedSeries) {
        if (updatedSeries == null || updatedSeries.getId() <= 0) {
            throw new IllegalArgumentException("Invalid series to update.");
        }
        seriesDao.save(updatedSeries);
    }

    public void deleteSeries(Long id) {
        Series series = seriesDao.findById(id);
        if (series != null) {
            seriesDao.delete(series);
        }
    }

    public List<Issue> getIssuesBySeries(Series series) {
        if (series == null || series.getId() == null) {
            throw new IllegalArgumentException("Invalid series provided.");
        }
        return seriesDao.findIssuesBySeries(series);
    }
}
