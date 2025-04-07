package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Character;
import com.tuka.comiccharacters.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CharacterDao {

    public void save(Character character) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(character);
            em.getTransaction().commit();
        }
    }

    public Character findById(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.find(Character.class, id);
        }
    }

    public List<Character> findAll() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT c FROM Character c", Character.class).getResultList();
        }
    }

    public void delete(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Character c = em.find(Character.class, id);
            if (c != null) {
                em.getTransaction().begin();
                em.remove(c);
                em.getTransaction().commit();
            }
        }
    }
}
