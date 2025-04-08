package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class IssueDaoImpl implements IssueDao {
    @Override
    public void save(Issue issue) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(issue);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Issue findById(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.find(Issue.class, id);
        }
    }

    @Override
    public List<Issue> findAll() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT c FROM Issue c", Issue.class).getResultList();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Issue c = em.find(Issue.class, id);
            if (c != null) {
                em.getTransaction().begin();
                em.remove(c);
                em.getTransaction().commit();
            }
        }
    }
}
