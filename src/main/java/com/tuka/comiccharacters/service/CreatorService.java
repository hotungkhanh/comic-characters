package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.CreatorDaoImpl;
import com.tuka.comiccharacters.dao.Dao;
import com.tuka.comiccharacters.model.Creator;

import java.util.List;
import java.util.Set;

public class CreatorService {
    private final Dao<Creator> creatorDao = new CreatorDaoImpl();

    public void addCreator(String name) {
        creatorDao.save(new Creator(name));
    }

    public void addCreator(String name, String overview) {
        creatorDao.save(new Creator(name, overview));
    }

    public Set<Creator> getAllCreators() {
        return creatorDao.findAll();
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
