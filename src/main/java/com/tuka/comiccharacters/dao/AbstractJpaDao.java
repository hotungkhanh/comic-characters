package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.util.JPAUtil;

import java.util.List;

public abstract class AbstractJpaDao<T> implements Dao<T> {
    private final Class<T> entityClass;

    protected AbstractJpaDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void save(T entity) {
        var em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public T findById(int id) {
        var em = JPAUtil.getEntityManagerFactory().createEntityManager();
        T entity = em.find(entityClass, id);
        em.close();
        return entity;
    }

    @Override
    public List<T> findAll() {
        var em = JPAUtil.getEntityManagerFactory().createEntityManager();
        var query = em.createQuery("FROM " + entityClass.getSimpleName(), entityClass);
        List<T> result = query.getResultList();
        em.close();
        return result;
    }

    @Override
    public void delete(T entity) {
        var em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        entity = em.merge(entity);
        em.remove(entity);
        em.getTransaction().commit();
        em.close();
    }
}

