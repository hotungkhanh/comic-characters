package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Series;

public class SeriesDaoImpl extends AbstractJpaDao<Series> {
    public SeriesDaoImpl() {
        super(Series.class);
    }
}
