package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Series;

import java.util.List;

public interface SeriesDao {
    void save(Series series);

    Series findById(Long id);

    List<Series> findAll();

    void deleteById(Long id);
}
