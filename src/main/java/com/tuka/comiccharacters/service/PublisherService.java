package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.PublisherDaoImpl;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;

import java.util.Set;

public class PublisherService {
    private final PublisherDaoImpl publisherDao = new PublisherDaoImpl();

    public void addPublisher(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Publisher name cannot be empty.");
        }
        Publisher publisher = new Publisher(name.trim());
        publisherDao.save(publisher);
    }

    public void addPublisher(String name, Set<Series> allSeries) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Publisher name cannot be empty.");
        }
        Publisher publisher = new Publisher(name.trim(), allSeries);
        publisherDao.save(publisher);
    }

    public Set<Publisher> getAllPublishers() {
        return publisherDao.findAll();
    }

    public Publisher getPublisherById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid publisher ID.");
        }
        return publisherDao.findById(id);
    }

    public Publisher getPublisherByIdWithSeriesAndCharacters(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid publisher ID.");
        }
        return publisherDao.findByIdWithSeriesAndCharacters(id);
    }

    public void updatePublisher(Publisher publisher) {
        if (publisher == null || publisher.getId() == null || publisher.getId() <= 0) {
            throw new IllegalArgumentException("Invalid publisher object for update.");
        }
        if (publisher.getName() == null || publisher.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Publisher name cannot be empty.");
        }
        publisher.setName(publisher.getName().trim());
        publisherDao.save(publisher);
    }

    public void deletePublisher(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid publisher ID for deletion.");
        }
        Publisher publisher = publisherDao.findById(id);
        if (publisher != null) {
            publisherDao.delete(publisher);
        }
    }
}