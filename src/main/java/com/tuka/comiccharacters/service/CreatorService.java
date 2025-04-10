package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.CreatorDaoImpl;
import com.tuka.comiccharacters.model.Creator;

import java.util.Collection;
import java.util.List;

public class CreatorService {
    private final CreatorDaoImpl creatorDao = new CreatorDaoImpl();

    public void addCreator(String name) {
        Creator creator = new Creator(name);
        creatorDao.save(creator);
    }

    public void addCreator(String name, String overview) {
        Creator creator = new Creator(name, overview);
        creatorDao.save(creator);
    }

    public List<Creator> getAllCreators() {
        return creatorDao.findAll();
    }
}
