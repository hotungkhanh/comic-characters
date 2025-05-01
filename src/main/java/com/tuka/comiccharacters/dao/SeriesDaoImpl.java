package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Series;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.HashSet;
import java.util.List;

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
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void delete(Series series) {
        EntityTransaction transaction = null;
        try (EntityManager em = getEntityManager()) {
            transaction = em.getTransaction();
            transaction.begin();

            // Merge the detached Series entity
            Series managedSeries = em.merge(series);

            if (managedSeries != null) {
                // Create a copy to avoid ConcurrentModificationException
                HashSet<Issue> issuesToRemove = new HashSet<>(managedSeries.getIssues());

                // First, handle references to issues as firstAppearance in ComicCharacter entities
                for (Issue issue : issuesToRemove) {
                    // Find all characters that reference this issue as firstAppearance
                    List<ComicCharacter> charactersWithFirstAppearance = em.createQuery(
                                    "SELECT c FROM ComicCharacter c WHERE c.firstAppearance.id = :issueId",
                                    ComicCharacter.class)
                            .setParameter("issueId", issue.getId())
                            .getResultList();

                    // Set firstAppearance to null for these characters
                    for (ComicCharacter character : charactersWithFirstAppearance) {
                        character.setFirstAppearance(null);
                    }
                }

                // Flush to ensure all character references are updated
                em.flush();

                for (Issue issue : issuesToRemove) {
                    // Merge the issue to ensure it's managed
                    Issue managedIssue = em.merge(issue);

                    // Remove the issue's association with characters
                    new HashSet<>(managedIssue.getCharacters()).forEach(character -> {
                        character.getIssues().remove(managedIssue);
                    });
                    managedIssue.getCharacters().clear();

                    // Remove the issue from the series's collection
                    // This will trigger orphanRemoval
                    managedSeries.getIssues().remove(managedIssue);

                    // No need for explicit em.remove() here
                }

                // Finally, delete the series
                em.remove(managedSeries);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting series " + series.toString() + " with error " + e.getMessage(), e);
        }
    }
}
