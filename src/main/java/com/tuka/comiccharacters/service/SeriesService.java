package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.SeriesDaoImpl;
import com.tuka.comiccharacters.model.Series;

public class SeriesService {
    private final SeriesDaoImpl seriesDao = new SeriesDaoImpl();

    public void addSeries(String title, int startYear) {
        Series series = new Series(title, startYear);
        seriesDao.save(series);
    }
}
