package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class SeriesDaoImpl implements SeriesDao {
    public void save(Series book) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(book);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Series findById(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.find(Series.class, id);
        }
    }

    @Override
    public List<Series> findAll() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT c FROM Series c", Series.class).getResultList();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Series c = em.find(Series.class, id);
            if (c != null) {
                em.getTransaction().begin();
                em.remove(c);
                em.getTransaction().commit();
            }
        }
    }
}
