package com.tuka.comiccharacters.ui;

import com.tuka.comiccharacters.model.ComicBook;
import com.tuka.comiccharacters.util.JPAUtil;
import jakarta.persistence.EntityManager;

public class MainApp {
    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        em.getTransaction().begin();

        ComicBook comic = new ComicBook("Aquaman", 1940);
        em.persist(comic);

        em.getTransaction().commit();
        em.close();
    }
}