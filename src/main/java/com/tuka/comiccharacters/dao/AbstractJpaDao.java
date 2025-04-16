package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public abstract class AbstractJpaDao<T> implements Dao<T> {
    private final Class<T> entityClass;

    protected AbstractJpaDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void save(T entity) {
        try (EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager()) {
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
        try (EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager()) {
            return em.find(entityClass, id);
        }
    }

    @Override
    public List<T> findAll() {
        try (EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("FROM " + entityClass.getSimpleName(), entityClass)
                    .getResultList();
        }
    }

    @Override
    public void delete(T entity) {
        try (EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager()) {
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
