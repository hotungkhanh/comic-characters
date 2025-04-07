package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.util.JPAUtil;
import jakarta.persistence.EntityManager;

public class SeriesDao {
    public void save(Series book) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(book);
        em.getTransaction().commit();
        em.close();
    }
}
