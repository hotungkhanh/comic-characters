package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractJpaDao<T> implements Dao<T> {
    private final Class<T> entityClass;

    protected AbstractJpaDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager getEntityManager() {
        return JPAUtil.getEntityManager();
    }

    @Override
    public void save(T entity) {
        try (EntityManager em = getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(entity); // merge handles both insert and update
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public T findById(Long id) {
        try (EntityManager em = getEntityManager()) {
            return em.find(entityClass, id);
        }
    }

    @Override
    public Set<T> findAll() {
        try (EntityManager em = getEntityManager()) {
            List<T> resultList = em.createQuery("FROM " + entityClass.getSimpleName(), entityClass)
                    .getResultList();
            return new HashSet<>(resultList);  // Convert to Set
        }
    }


    @Override
    public void delete(T entity) {
        try (EntityManager em = getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                T managedEntity = em.merge(entity); // ensure attached to context
                em.remove(managedEntity);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
}
