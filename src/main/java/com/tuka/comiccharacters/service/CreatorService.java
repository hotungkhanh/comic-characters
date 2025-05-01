package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.CreatorDaoImpl;
import com.tuka.comiccharacters.model.Creator;

public class CreatorService extends AbstractService<Creator> {

    public CreatorService() {
        super(new CreatorDaoImpl());
    }

    @Override
    protected void validateEntity(Creator creator) {
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }

        String name = creator.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Creator name cannot be null or blank");
        }

        name = name.trim();
        if (name.length() > 255) {
            throw new IllegalArgumentException("Creator name must be 255 characters or fewer");
        }
        creator.setName(name);

        if (creator.getOverview() != null && creator.getOverview().length() > 1000) {
            throw new IllegalArgumentException("Overview must be 1000 characters or fewer");
        }

        if (creator.getImageUrl() != null && creator.getImageUrl().length() > 2083) {
            throw new IllegalArgumentException("Image URL must be 2083 characters or fewer");
        }

        if (creator.getId() != null && creator.getId() <= 0) {
            throw new IllegalArgumentException("Invalid creator ID");
        }
    }
}
