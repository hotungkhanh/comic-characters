package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Series;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.tuka.comiccharacters.util.JPAUtil.getEntityManager;

public class SeriesDaoImpl extends AbstractJpaDao<Series> {
    public SeriesDaoImpl() {
        super(Series.class);
    }

    public Series findByIdWithIssuesAndPublisher(Long id) {
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

    public List<Issue> findIssuesBySeries(Series series) {
        try (EntityManager em = getEntityManager()) {
            return em.createQuery("SELECT i FROM Issue i WHERE i.series = :series", Issue.class)
                    .setParameter("series", series)
                    .getResultList();
        }
    }
}
