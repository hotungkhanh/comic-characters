package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Series;
import jakarta.persistence.EntityManager;

public class SeriesDaoImpl extends AbstractJpaDao<Series> {
    public SeriesDaoImpl() {
        super(Series.class);
    }

    @Override
    public Series findByIdWithDetails(Long id) {
        try (EntityManager em = getEntityManager()) {
            return em.createQuery(
                            "SELECT s FROM Series s " +
                                    "LEFT JOIN FETCH s.publisher " +
                                    "LEFT JOIN FETCH s.issues " +
                                    "WHERE s.id = :id", Series.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        }
    }
}
