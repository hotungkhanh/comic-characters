package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PublisherDaoImpl implements PublisherDao {
    @Override
    public void save(Publisher publisher) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(publisher);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Publisher findById(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.find(Publisher.class, id);
        }
    }

    @Override
    public List<Publisher> findAll() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT c FROM Publisher c", Publisher.class).getResultList();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Publisher c = em.find(Publisher.class, id);
            if (c != null) {
                em.getTransaction().begin();
                em.remove(c);
                em.getTransaction().commit();
            }
        }
    }
}
