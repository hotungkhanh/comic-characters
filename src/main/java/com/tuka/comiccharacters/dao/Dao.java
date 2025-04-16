package com.tuka.comiccharacters.dao;

import java.util.List;

public interface Dao<T> {
    void save(T entity);

    T findById(Long id);

    List<T> findAll();

    void delete(T entity);
}