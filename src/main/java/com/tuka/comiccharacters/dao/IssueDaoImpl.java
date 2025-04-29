package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.IssueCreator;
import com.tuka.comiccharacters.model.Series;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.HashSet;
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

    @Override
    public void save(Issue issue) {
        EntityTransaction transaction = null;
        try (EntityManager em = getEntityManager()) {
            transaction = em.getTransaction();
            transaction.begin();

            // First, detach the creators and characters to handle later
            Set<IssueCreator> detachedCreators = new HashSet<>(issue.getIssueCreators());
            Set<ComicCharacter> detachedCharacters = new HashSet<>(issue.getCharacters());

            issue.getIssueCreators().clear();
            issue.getCharacters().clear();

            // Persist or merge the Issue first
            if (issue.getId() == null) {
                em.persist(issue);
            } else {
                issue = em.merge(issue); // reattach the managed version
            }

            // Set and persist the IssueCreators (they require a non-null issue)
            setIssueCreators(em, issue, detachedCreators);

            // Associate Characters with the Issue
            setIssueCharacters(em, issue, detachedCharacters);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving issue: " + e.getMessage(), e);
        }
    }

    private void setIssueCreators(EntityManager em, Issue issue, Set<IssueCreator> issueCreators) {
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

    private void setIssueCharacters(EntityManager em, Issue issue, Set<ComicCharacter> characters) {
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
                removeFromSeries(em, managedIssue);
                removeFromCharacters(em, managedIssue);
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

    private void removeFromSeries(EntityManager em, Issue issue) {
        Series series = issue.getSeries();
        if (series != null) {
            Series managedSeries = em.find(Series.class, series.getId()); //get managed version.
            if (managedSeries != null && managedSeries.getIssues() != null) {
                managedSeries.getIssues().remove(issue);
            }
        }
    }

    private void removeFromCharacters(EntityManager em, Issue issue) {
        for (ComicCharacter character : new HashSet<>(issue.getCharacters())) {
            ComicCharacter managedCharacter = em.find(ComicCharacter.class, character.getId());
            if (managedCharacter != null) {
                managedCharacter.getIssues().remove(issue);
            }

        }
        issue.getCharacters().clear();
    }
}
