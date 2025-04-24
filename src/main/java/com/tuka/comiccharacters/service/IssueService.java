package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.IssueDaoImpl;
import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.IssueCreator;
import com.tuka.comiccharacters.model.Series;

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
                         BigDecimal priceUsd, boolean isAnnual, List<IssueCreator> issueCreators,
                         List<ComicCharacter> characters) {
        try {
            executeInTransaction(em -> {
                // Create the new issue with basic properties
                Issue issue = new Issue(series, issueNumber);
                issue.setOverview(overview);
                issue.setReleaseDate(releaseDate);
                issue.setPriceUsd(priceUsd);
                issue.setAnnual(isAnnual);

                // Handle issue creators
                if (issueCreators != null && !issueCreators.isEmpty()) {
                    for (IssueCreator ic : issueCreators) {
                        ic.setIssue(issue);
                    }
                    issue.setIssueCreators(new HashSet<>(issueCreators));
                }

                // Handle characters - retrieve managed instances from the session
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

                // Save the issue
                em.persist(issue);
            });
        } catch (Exception e) {
            throw new RuntimeException("Error adding issue: " + e.getMessage(), e);
        }
    }

    public void updateIssue(Issue existingIssue) {
        try {
            executeInTransaction(em -> {
                // Retrieve a managed instance of the issue
                Issue managedIssue = em.find(Issue.class, existingIssue.getId());
                if (managedIssue == null) {
                    throw new IllegalArgumentException("Issue not found: " + existingIssue.getId());
                }

                // Update basic properties
                managedIssue.setIssueNumber(existingIssue.getIssueNumber());
                managedIssue.setOverview(existingIssue.getOverview());
                managedIssue.setReleaseDate(existingIssue.getReleaseDate());
                managedIssue.setPriceUsd(existingIssue.getPriceUsd());
                managedIssue.setAnnual(existingIssue.getAnnual());

                // Update creators
                managedIssue.getIssueCreators().clear();
                Set<IssueCreator> updatedIssueCreators = existingIssue.getIssueCreators();
                if (updatedIssueCreators != null) {
                    for (IssueCreator ic : updatedIssueCreators) {
                        ic.setIssue(managedIssue);
                        em.merge(ic);
                    }
                    managedIssue.getIssueCreators().addAll(updatedIssueCreators);
                }

                // Update characters
                Set<ComicCharacter> existingCharacters = managedIssue.getCharacters();
                // First remove this issue from all existing characters
                for (ComicCharacter character : existingCharacters) {
                    character.getIssues().remove(managedIssue);
                }
                existingCharacters.clear();

                // Then add the new characters
                Set<ComicCharacter> updatedCharacters = existingIssue.getCharacters();
                if (updatedCharacters != null) {
                    for (ComicCharacter character : updatedCharacters) {
                        ComicCharacter managedCharacter = em.find(ComicCharacter.class, character.getId());
                        if (managedCharacter != null) {
                            managedCharacter.getIssues().add(managedIssue);
                            existingCharacters.add(managedCharacter);
                        }
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Error updating issue: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        validateId(id);

        try {
            executeInTransaction(em -> {
                Issue issue = em.find(Issue.class, id);
                if (issue == null) {
                    throw new IllegalArgumentException("Issue with ID " + id + " not found.");
                }

                // Remove this issue from its series
                Series series = issue.getSeries();
                if (series != null && series.getIssues() != null) {
                    series.getIssues().remove(issue);
                }

                // Remove this issue from associated characters
                for (ComicCharacter character : new HashSet<>(issue.getCharacters())) {
                    character.getIssues().remove(issue);
                }
                issue.getCharacters().clear();

                // Finally, delete the issue
                em.remove(issue);
            });
        } catch (Exception e) {
            throw new RuntimeException("Error deleting issue with ID " + id, e);
        }
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
