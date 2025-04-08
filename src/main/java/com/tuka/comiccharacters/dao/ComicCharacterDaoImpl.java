package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ComicCharacterDaoImpl implements ComicCharacterDao {

    @Override
    public void save(ComicCharacter character) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(character);
            em.getTransaction().commit();
        }
    }

    public ComicCharacter findById(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.find(ComicCharacter.class, id);
        }
    }

    public List<ComicCharacter> findAll() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT c FROM ComicCharacter c", ComicCharacter.class).getResultList();
        }
    }

    public void deleteById(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            ComicCharacter c = em.find(ComicCharacter.class, id);
            if (c != null) {
                em.getTransaction().begin();
                em.remove(c);
                em.getTransaction().commit();
            }
        }
    }
}
