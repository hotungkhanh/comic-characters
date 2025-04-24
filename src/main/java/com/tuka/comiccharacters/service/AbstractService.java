package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.Dao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import static com.tuka.comiccharacters.util.JPAUtil.getEntityManager;

public abstract class AbstractService<T> {

    protected final Dao<T> dao;

    protected AbstractService(Dao<T> dao) {
        this.dao = dao;
    }

    public T findById(Long id) {
        validateId(id);
        return dao.findById(id);
    }

    public T findByIdWithDetails(Long id) {
        validateId(id);
        return dao.findByIdWithDetails(id);
    }

    public void save(T entity) {
        validateEntity(entity);
        dao.save(entity);
    }

    public void delete(Long id) {
        validateId(id);
        T entity = findById(id);
        if (entity != null) {
            // Handle relationships before deletion
            dao.delete(entity);
        }
    }

    // Template method to be implemented by concrete services
    protected abstract void validateEntity(T entity);

    protected void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }
    }

    // Helper method for transaction management
    protected void executeInTransaction(TransactionCallback callback) throws Exception {
        EntityTransaction transaction = null;
        try (EntityManager em = getEntityManager()) {
            transaction = em.getTransaction();
            transaction.begin();

            callback.execute(em);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    // Functional interface for transaction callback
    @FunctionalInterface
    protected interface TransactionCallback {
        void execute(EntityManager em) throws Exception;
    }
}
