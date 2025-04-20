package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.PublisherDaoImpl;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;

import java.util.List;
import java.util.Set;

public class PublisherService {
    private final PublisherDaoImpl publisherDao = new PublisherDaoImpl();

    public void addPublisher(String name) {
        Publisher publisher = new Publisher(name);
        publisherDao.save(publisher);
    }

    public void addPublisher(String name, Set<Series> allSeries) {
        Publisher publisher = new Publisher(name, allSeries);
        publisherDao.save(publisher);
    }

    public Set<Publisher> getAllPublishers() {
        return publisherDao.findAll();
    }
}
