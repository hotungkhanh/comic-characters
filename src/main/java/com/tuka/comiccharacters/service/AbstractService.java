package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.Dao;

import java.util.Set;

public abstract class AbstractService<T> {

    protected final Dao<T> dao;

    protected AbstractService(Dao<T> dao) {
        this.dao = dao;
    }

    public Set<T> getAllEntities() {
        return dao.findAll();
    }

    public T getById(Long id) {
        validateId(id);
        return dao.findById(id);
    }

    public T getByIdWithDetails(Long id) {
        validateId(id);
        return dao.findByIdWithDetails(id);
    }

    public void save(T entity) {
        validateEntity(entity);
        dao.save(entity);
    }

    public void delete(Long id) {
        validateId(id);
        T entity = getById(id);
        if (entity != null) {
            // Needs to handle relationships before deletion
            dao.delete(entity);
        }
    }

    protected abstract void validateEntity(T entity);

    protected void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }
    }
}
