package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.PublisherDaoImpl;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;

import java.util.List;

public class PublisherService {
    private final PublisherDaoImpl publisherDao = new PublisherDaoImpl();

    public void addPublisher(String name) {
        Publisher publisher = new Publisher(name);
        publisherDao.save(publisher);
    }

    public void addPublisher(String name, List<Series> seriesList) {
        Publisher publisher = new Publisher(name, seriesList);
        publisherDao.save(publisher);
    }

    public List<Publisher> getAllPublishers() {
        return publisherDao.findAll();
    }
}
