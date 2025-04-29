package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Publisher;
import jakarta.persistence.EntityManager;

public class PublisherDaoImpl extends AbstractJpaDao<Publisher> {
    public PublisherDaoImpl() {
        super(Publisher.class);
    }

    @Override
    public Publisher findByIdWithDetails(Long id) {
        try (EntityManager em = getEntityManager()) {
            return em.createQuery("SELECT p FROM Publisher p LEFT JOIN FETCH p.publisherSeries LEFT JOIN FETCH p.publisherCharacters WHERE p.id = :id", Publisher.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
