package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Creator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.HashSet;
import java.util.Set;

public class CreatorDaoImpl extends AbstractJpaDao<Creator> {
    public CreatorDaoImpl() {
        super(Creator.class);
    }

    public Creator findByIdWithDetails(Long id) {
        try (EntityManager em = getEntityManager()) {
            return em.createQuery(
                            "SELECT c FROM Creator c LEFT JOIN FETCH c.creditedCharacters LEFT JOIN FETCH c.issueCreators ic LEFT JOIN FETCH ic.issue WHERE c.id = :id",
                            Creator.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void delete(Creator creator) {
        EntityTransaction transaction = null;
        try (EntityManager em = getEntityManager()) {
            transaction = em.getTransaction();
            transaction.begin();

            Creator managedCreator = em.find(Creator.class, creator.getId());
            if (managedCreator != null) {
                // Remove the association with credited characters
                Set<ComicCharacter> creditedCharacters = managedCreator.getCreditedCharacters();
                // To avoid ConcurrentModificationException, iterate over a copy
                for (ComicCharacter character : new HashSet<>(creditedCharacters)) {
                    character.getCreators().remove(managedCreator); // Use managedCreator here
                }
                managedCreator.getCreditedCharacters().clear(); // Clear the set on the Creator

                em.remove(managedCreator);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting creator: " + e.getMessage(), e);
        }
    }
}
