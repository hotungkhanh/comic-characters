package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.SeriesDao;
import com.tuka.comiccharacters.model.Series;

public class SeriesService {
    private final SeriesDao seriesDao = new SeriesDao();

    public void addComicBook(String title, int startYear) {
        Series series = new Series(title, startYear);
        seriesDao.save(series);
    }
}
