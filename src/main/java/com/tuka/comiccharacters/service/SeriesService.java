package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.SeriesDaoImpl;
import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;

import java.util.List;

public class SeriesService {
    private final SeriesDaoImpl seriesDao = new SeriesDaoImpl();

    public void addSeries(String title, int startYear) {
        addSeries(title, startYear, null);
    }

    public void addSeries(String title, int startYear, Publisher publisher) {
        Series series = new Series(title, startYear, publisher);
        seriesDao.save(series);
    }

    public List<Series> getAllSeries() {
        return seriesDao.findAll();
    }

    public Series getByIdWithIssues(Long id) {
        return seriesDao.findByIdWithIssues(id);
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
}
