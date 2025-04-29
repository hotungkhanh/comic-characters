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
        if (creator.getName() == null || creator.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Creator name cannot be empty");
        }
    }
}
