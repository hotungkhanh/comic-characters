package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.PublisherDaoImpl;
import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Set;

import static com.tuka.comiccharacters.util.JPAUtil.getEntityManager;

public class PublisherService {
    private final PublisherDaoImpl publisherDao = new PublisherDaoImpl();

    public void addPublisher(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Publisher name cannot be empty.");
        }
        Publisher publisher = new Publisher(name.trim());
        publisherDao.save(publisher);
    }

    public Set<Publisher> getAllPublishers() {
        return publisherDao.findAll();
    }

    public Publisher getPublisherByIdWithSeriesAndCharacters(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid publisher ID.");
        }
        return publisherDao.findByIdWithDetails(id);
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
        EntityManager em = getEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();

            Publisher publisher = em.find(Publisher.class, id);
            if (publisher != null) {
                // Remove the association with characters
                Set<ComicCharacter> publisherCharacters = publisher.getPublisherCharacters();
                for (ComicCharacter character : new java.util.HashSet<>(publisherCharacters)) {
                    character.setPublisher(null);
                }
                publisher.getPublisherCharacters().clear();

                // Remove the association with series
                Set<Series> publisherSeries = publisher.getPublisherSeries();
                for (Series series : new java.util.HashSet<>(publisherSeries)) {
                    series.setPublisher(null);
                }
                publisher.getPublisherSeries().clear();

                em.remove(publisher);
            } else {
                throw new IllegalArgumentException("Publisher with ID " + id + " not found.");
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
