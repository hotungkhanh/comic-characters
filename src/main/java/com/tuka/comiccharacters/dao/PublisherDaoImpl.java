package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.HashSet;
import java.util.Set;

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

    @Override
    public void delete(Publisher publisher) {
        EntityTransaction transaction = null;
        try (EntityManager em = getEntityManager()) {

            transaction = em.getTransaction();
            transaction.begin();

            // Use the passed-in publisher object
            Publisher managedPublisher = em.find(Publisher.class, publisher.getId());
            if (managedPublisher != null) {
                // Remove the association with characters
                Set<ComicCharacter> publisherCharacters = managedPublisher.getPublisherCharacters();
                for (ComicCharacter character : new HashSet<>(publisherCharacters)) {
                    character.setPublisher(null);
                }
                managedPublisher.getPublisherCharacters().clear();

                // Remove the association with series
                Set<Series> publisherSeries = managedPublisher.getPublisherSeries();
                for (Series series : new HashSet<>(publisherSeries)) {
                    series.setPublisher(null);
                }
                managedPublisher.getPublisherSeries().clear();

                em.remove(managedPublisher);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting publisher " + publisher.toString() + " with error " + e.getMessage(), e);
        }
    }
}
