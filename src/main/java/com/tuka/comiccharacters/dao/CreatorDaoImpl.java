package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Creator;
import jakarta.persistence.EntityManager;

public class CreatorDaoImpl extends AbstractJpaDao<Creator> {
    public CreatorDaoImpl() {
        super(Creator.class);
    }

    public Creator findByIdWithDetails(Long id) {
        try (EntityManager em = getEntityManager()) {
            return em.createQuery("SELECT c FROM Creator c LEFT JOIN FETCH c.creditedCharacters LEFT JOIN FETCH c.issueCreators ic LEFT JOIN FETCH ic.issue WHERE c.id = :id", Creator.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        }
    }
}
