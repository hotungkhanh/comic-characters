package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.IssueDaoImpl;
import com.tuka.comiccharacters.model.*;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IssueService extends AbstractService<Issue> {

    public IssueService() {
        super(new IssueDaoImpl());
    }

    public void addIssue(Series series, BigDecimal issueNumber, String overview, LocalDate releaseDate,
                         BigDecimal priceUsd, String imageUrl, boolean isAnnual,
                         List<IssueCreator> issueCreators, List<ComicCharacter> characters) {
        try {
            executeInTransaction(em -> {
                Issue issue = createBasicIssue(series, issueNumber, overview, releaseDate, priceUsd, imageUrl, isAnnual);
                setIssueCreators(issue, issueCreators);
                setIssueCharacters(em, issue, characters);
                em.persist(issue);
            });
        } catch (Exception e) {
            throw new RuntimeException("Error adding issue: " + e.getMessage(), e);
        }
    }

    private Issue createBasicIssue(Series series, BigDecimal issueNumber, String overview,
                                   LocalDate releaseDate, BigDecimal priceUsd,
                                   String imageUrl, boolean isAnnual) {
        Issue issue = new Issue(series, issueNumber);
        issue.setOverview(overview);
        issue.setReleaseDate(releaseDate);
        issue.setPriceUsd(priceUsd);
        issue.setImageUrl(imageUrl);
        issue.setAnnual(isAnnual);
        return issue;
    }

    private void setIssueCreators(Issue issue, List<IssueCreator> issueCreators) {
        if (issueCreators != null && !issueCreators.isEmpty()) {
            for (IssueCreator ic : issueCreators) {
                ic.setIssue(issue);
            }
            issue.setIssueCreators(new HashSet<>(issueCreators));
        }
    }

    private void setIssueCharacters(EntityManager em, Issue issue, List<ComicCharacter> characters) {
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

    public void updateIssue(Issue existingIssue) {
        try {
            executeInTransaction(em -> {
                Issue managedIssue = findAndValidateIssue(em, existingIssue.getId());
                updateBasicProperties(managedIssue, existingIssue);
                updateIssueCreators(em, managedIssue, existingIssue.getIssueCreators());
                updateIssueCharacters(em, managedIssue, existingIssue.getCharacters());
            });
        } catch (Exception e) {
            throw new RuntimeException("Error updating issue: " + e.getMessage(), e);
        }
    }

    private Issue findAndValidateIssue(EntityManager em, Long issueId) {
        Issue managedIssue = em.find(Issue.class, issueId);
        if (managedIssue == null) {
            throw new IllegalArgumentException("Issue not found: " + issueId);
        }
        return managedIssue;
    }

    private void updateBasicProperties(Issue managedIssue, Issue sourceIssue) {
        managedIssue.setIssueNumber(sourceIssue.getIssueNumber());
        managedIssue.setOverview(sourceIssue.getOverview());
        managedIssue.setReleaseDate(sourceIssue.getReleaseDate());
        managedIssue.setPriceUsd(sourceIssue.getPriceUsd());
        managedIssue.setImageUrl(sourceIssue.getImageUrl());
        managedIssue.setAnnual(sourceIssue.getAnnual());
    }

    private void updateIssueCreators(EntityManager em, Issue managedIssue, Set<IssueCreator> updatedIssueCreators) {
        // Remove old creators
        Set<IssueCreator> oldCreators = new HashSet<>(managedIssue.getIssueCreators());
        for (IssueCreator ic : oldCreators) {
            managedIssue.getIssueCreators().remove(ic);
            em.remove(ic);
        }

        // Add new creators
        if (updatedIssueCreators != null) {
            for (IssueCreator ic : updatedIssueCreators) {
                IssueCreator newIc = new IssueCreator();
                newIc.setIssue(managedIssue);
                newIc.setCreator(em.find(Creator.class, ic.getCreator().getId()));
                newIc.setRoles(ic.getRoles());
                em.persist(newIc);
                managedIssue.getIssueCreators().add(newIc);
            }
        }
    }

    private void updateIssueCharacters(EntityManager em, Issue managedIssue, Set<ComicCharacter> updatedCharacters) {
        Set<ComicCharacter> existingCharacters = managedIssue.getCharacters();

        // Remove issue from all existing characters
        for (ComicCharacter character : existingCharacters) {
            character.getIssues().remove(managedIssue);
        }
        existingCharacters.clear();

        // Add the new characters
        if (updatedCharacters != null) {
            for (ComicCharacter character : updatedCharacters) {
                ComicCharacter managedCharacter = em.find(ComicCharacter.class, character.getId());
                if (managedCharacter != null) {
                    managedCharacter.getIssues().add(managedIssue);
                    existingCharacters.add(managedCharacter);
                }
            }
        }
    }

    @Override
    public void delete(Long id) {
        validateId(id);
        try {
            executeInTransaction(em -> {
                Issue issue = findAndValidateIssue(em, id);
                removeFromSeries(issue);
                removeFromCharacters(issue);
                em.remove(issue);
            });
        } catch (Exception e) {
            throw new RuntimeException("Error deleting issue with ID " + id, e);
        }
    }

    private void removeFromSeries(Issue issue) {
        Series series = issue.getSeries();
        if (series != null && series.getIssues() != null) {
            series.getIssues().remove(issue);
        }
    }

    private void removeFromCharacters(Issue issue) {
        for (ComicCharacter character : new HashSet<>(issue.getCharacters())) {
            character.getIssues().remove(issue);
        }
        issue.getCharacters().clear();
    }

    @Override
    protected void validateEntity(Issue issue) {
        if (issue == null) {
            throw new IllegalArgumentException("Issue cannot be null");
        }
        if (issue.getSeries() == null) {
            throw new IllegalArgumentException("Issue must be associated with a series");
        }
        if (issue.getIssueNumber() == null) {
            throw new IllegalArgumentException("Issue number cannot be null");
        }
    }
}
