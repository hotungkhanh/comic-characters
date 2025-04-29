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
