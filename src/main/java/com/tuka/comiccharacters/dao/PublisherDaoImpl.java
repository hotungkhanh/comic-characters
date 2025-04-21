package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Publisher;
import jakarta.persistence.EntityManager;

import static com.tuka.comiccharacters.util.JPAUtil.getEntityManager;

public class PublisherDaoImpl extends AbstractJpaDao<Publisher> {
    public PublisherDaoImpl() {
        super(Publisher.class);
    }

    public Publisher findByIdWithSeriesAndCharacters(Long id) {
        try (EntityManager em = getEntityManager()) {
            return em.createQuery("SELECT p FROM Publisher p LEFT JOIN FETCH p.publisherSeries LEFT JOIN FETCH p.publisherCharacters WHERE p.id = :id", Publisher.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        }
    }
}
