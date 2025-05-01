package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.PublisherDaoImpl;
import com.tuka.comiccharacters.model.Publisher;

public class PublisherService extends AbstractService<Publisher> {

    public PublisherService() {
        super(new PublisherDaoImpl());
    }

    @Override
    protected void validateEntity(Publisher publisher) {
        if (publisher == null) {
            throw new IllegalArgumentException("Publisher cannot be null");
        }

        String name = publisher.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Publisher name cannot be null or blank");
        }

        // Trim and set cleaned name
        publisher.setName(name.trim());

        // ID must be null (for new entities) or positive (for existing ones)
        if (publisher.getId() != null && publisher.getId() <= 0) {
            throw new IllegalArgumentException("Publisher ID must be positive if present");
        }

        if (publisher.getName().length() > 255) {
            throw new IllegalArgumentException("Publisher name must be 255 characters or fewer");
        }
    }
}
