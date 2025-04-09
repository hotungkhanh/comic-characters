package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Publisher;

import java.util.List;

public interface PublisherDao {
    void save(Publisher publisher);

    Publisher findById(Long id);

    List<Publisher> findAll();

    void deleteById(Long id);
}
