package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Issue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class IssueDaoImpl extends AbstractJpaDao<Issue> {
    public IssueDaoImpl() {
        super(Issue.class);
    }

    public Issue findByIdWithDetails(Long id) {
        try (EntityManager em = getEntityManager()) {
            TypedQuery<Issue> query = em.createQuery(
                    "SELECT i FROM Issue i " +
                            "LEFT JOIN FETCH i.issueCreators ic " +
                            "LEFT JOIN FETCH ic.creator " +
                            "LEFT JOIN FETCH ic.roles " +
                            "LEFT JOIN FETCH i.characters " +
                            "LEFT JOIN FETCH i.series s " +
                            "LEFT JOIN FETCH s.issues " +
                            "LEFT JOIN FETCH s.publisher " +
                            "WHERE i.id = :id", Issue.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}