package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.SeriesDaoImpl;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;

import java.util.List;

public class SeriesService {
    private final SeriesDaoImpl seriesDao = new SeriesDaoImpl();

    public void addSeries(String title, int startYear) {
        Series series = new Series(title, startYear);
        seriesDao.save(series);
    }

    public void addSeries(String title, int startYear, Publisher publisher) {
        Series series = new Series(title, startYear, publisher);
        seriesDao.save(series);
    }

    public List<Series> getAllSeries() {
        return seriesDao.findAll();
    }

    public Series getByIdWithIssues(int id) {
        return seriesDao.findByIdWithIssues(id);
    }

}
