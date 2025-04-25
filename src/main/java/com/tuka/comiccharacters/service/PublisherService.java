package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.PublisherDaoImpl;
import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;

import java.util.HashSet;
import java.util.Set;

public class PublisherService extends AbstractService<Publisher> {

    public PublisherService() {
        super(new PublisherDaoImpl());
    }

    public void addPublisher(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Publisher name cannot be empty.");
        }
        Publisher publisher = new Publisher(name.trim());
        save(publisher);
    }

    public void updatePublisher(Publisher publisher) {
        save(publisher);
    }

    @Override
    public void delete(Long id) {
        validateId(id);

        try {
            executeInTransaction(em -> {
                Publisher publisher = em.find(Publisher.class, id);
                if (publisher != null) {
                    // Remove the association with characters
                    Set<ComicCharacter> publisherCharacters = publisher.getPublisherCharacters();
                    for (ComicCharacter character : new HashSet<>(publisherCharacters)) {
                        character.setPublisher(null);
                    }
                    publisher.getPublisherCharacters().clear();

                    // Remove the association with series
                    Set<Series> publisherSeries = publisher.getPublisherSeries();
                    for (Series series : new HashSet<>(publisherSeries)) {
                        series.setPublisher(null);
                    }
                    publisher.getPublisherSeries().clear();

                    em.remove(publisher);
                } else {
                    throw new IllegalArgumentException("Publisher with ID " + id + " not found.");
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Error deleting publisher with ID " + id, e);
        }
    }

    @Override
    protected void validateEntity(Publisher publisher) {
        if (publisher == null) {
            throw new IllegalArgumentException("Publisher cannot be null");
        }
        if (publisher.getName() == null || publisher.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Publisher name cannot be empty");
        }

        // For existing publishers, also validate the ID
        if (publisher.getId() != null && publisher.getId() <= 0) {
            throw new IllegalArgumentException("Invalid publisher ID");
        }

        // Ensure the name is properly trimmed
        publisher.setName(publisher.getName().trim());
    }
}
