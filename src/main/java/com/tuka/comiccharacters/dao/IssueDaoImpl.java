package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.IssueCreator;
import com.tuka.comiccharacters.model.Series;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public void saveIssue(Issue issue, List<IssueCreator> issueCreators, List<ComicCharacter> characters) {
        EntityTransaction transaction = null;
        try (EntityManager em = getEntityManager()) {
            transaction = em.getTransaction();
            transaction.begin();

            setIssueCreators(em, issue, issueCreators); // Pass EntityManager
            setIssueCharacters(em, issue, characters);
            if (issue.getId() == null) {
                em.persist(issue);
            } else {
                em.merge(issue);
            }


            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving issue: " + e.getMessage(), e);
        }
    }

    private void setIssueCreators(EntityManager em, Issue issue, List<IssueCreator> issueCreators) { // Add EntityManager
        if (issueCreators != null && !issueCreators.isEmpty()) {
            for (IssueCreator ic : issueCreators) {
                ic.setIssue(issue);
                if (ic.getId() == null) {
                    em.persist(ic);  // Persist new IssueCreator instances
                }
            }
            issue.setIssueCreators(new HashSet<>(issueCreators));
        }
    }

    private void setIssueCharacters(EntityManager em, Issue issue, List<ComicCharacter> characters) { // Add EntityManager
        if (characters != null && !characters.isEmpty()) {
            Set<ComicCharacter> characterSet = new HashSet<>();
            for (ComicCharacter character : characters) {
                ComicCharacter managedCharacter = em.find(ComicCharacter.class, character.getId());
                if (managedCharacter != null) {
                    characterSet.add(managedCharacter);
                    managedCharacter.getIssues().add(issue);
                }
            }
            issue.setCharacters(characterSet);
        }
    }

    @Override
    public void delete(Issue issue) {
        EntityTransaction transaction = null;
        try (EntityManager em = getEntityManager()) {
            transaction = em.getTransaction();
            transaction.begin();

            Issue managedIssue = em.find(Issue.class, issue.getId());
            if (managedIssue != null) {
                removeFromSeries(managedIssue, em);
                removeFromCharacters(managedIssue, em);
                em.remove(managedIssue);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting issue with ID " + issue.getId(), e);
        }
    }

    private void removeFromSeries(Issue issue, EntityManager em) {
        Series series = issue.getSeries();
        if (series != null) {
            Series managedSeries = em.find(Series.class, series.getId()); //get managed version.
            if (managedSeries != null && managedSeries.getIssues() != null) {
                managedSeries.getIssues().remove(issue);
            }
        }
    }

    private void removeFromCharacters(Issue issue, EntityManager em) {
        for (ComicCharacter character : new HashSet<>(issue.getCharacters())) {
            ComicCharacter managedCharacter = em.find(ComicCharacter.class, character.getId());
            if (managedCharacter != null) {
                managedCharacter.getIssues().remove(issue);
            }

        }
        issue.getCharacters().clear();
    }
}
