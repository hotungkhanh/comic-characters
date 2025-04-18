package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.SeriesDaoImpl;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;

import java.util.List;

public class SeriesService {
    private final SeriesDaoImpl seriesDao = new SeriesDaoImpl();

    public void addSeries(String title, Integer startYear, Integer endYear, String overview, Publisher publisher) {
        Series series = new Series(title, startYear, endYear, overview, publisher);
        seriesDao.save(series);
    }

    public List<Series> getAllSeries() {
        return seriesDao.findAll();
    }

    public Series getByIdWithIssues(Long id) {
        return seriesDao.findByIdWithIssuesAndPublisher(id);
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
