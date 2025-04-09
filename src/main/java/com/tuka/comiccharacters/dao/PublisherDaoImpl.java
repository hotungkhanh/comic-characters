package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Publisher;

public class PublisherDaoImpl extends AbstractJpaDao<Publisher> {
    public PublisherDaoImpl() {
        super(Publisher.class);
    }
}
