package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.CreatorDaoImpl;
import com.tuka.comiccharacters.model.Creator;

import java.util.Set;

public class CreatorService {
    private final CreatorDaoImpl creatorDao = new CreatorDaoImpl();

    public void addCreator(String name, String overview) {
        creatorDao.save(new Creator(name, overview));
    }

    public Set<Creator> getAllCreators() {
        return creatorDao.findAll();
    }

    public Creator getCreatorByIdWithDetails(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid creator ID.");
        }
        return creatorDao.findByIdWithDetails(id);
    }

    public void updateCreator(Creator creator) {
        creatorDao.save(creator);
    }

    public void deleteCreator(Long id) {
        Creator creator = creatorDao.findById(id);
        if (creator != null) {
            creatorDao.delete(creator);
        }
    }
}
