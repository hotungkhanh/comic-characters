package com.tuka.comiccharacters.dao;

import java.util.Set;

public interface Dao<T> {
    void save(T entity);

    T findById(Long id);

    T findByIdWithDetails(Long id);

    Set<T> findAll();

    void delete(T entity);
}