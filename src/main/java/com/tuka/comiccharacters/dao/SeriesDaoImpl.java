package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Series;
import jakarta.persistence.EntityManager;

import static com.tuka.comiccharacters.util.JPAUtil.getEntityManager;

public class SeriesDaoImpl extends AbstractJpaDao<Series> {
    public SeriesDaoImpl() {
        super(Series.class);
    }

    public Series findByIdWithIssues(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT s FROM Series s LEFT JOIN FETCH s.issues WHERE s.id = :id", Series.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

}
