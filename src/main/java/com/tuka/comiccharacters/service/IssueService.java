package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.IssueDaoImpl;
import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.IssueCreator;
import com.tuka.comiccharacters.model.Series;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tuka.comiccharacters.util.JPAUtil.getEntityManager;

public class IssueService {
    private final IssueDaoImpl issueDao = new IssueDaoImpl();
    public void addIssue(Series series, BigDecimal issueNumber, String overview, LocalDate releaseDate, BigDecimal priceUsd, List<IssueCreator> issueCreators, List<ComicCharacter> characters) {
        Issue issue = new Issue(series, issueNumber);
        issue.setOverview(overview);
        issue.setReleaseDate(releaseDate);
        issue.setPriceUsd(priceUsd);

        if (issueCreators != null && !issueCreators.isEmpty()) {
            for (IssueCreator ic : issueCreators) {
                ic.setIssue(issue);
            }
            issue.setIssueCreators(new HashSet<>(issueCreators));
        }

        if (characters != null && !characters.isEmpty()) {
            Set<ComicCharacter> characterSet = new HashSet<>(characters);
            issue.setCharacters(characterSet);
            for (ComicCharacter c : characters) {
                c.getIssues().add(issue);
            }
        }

        issueDao.save(issue);
    }

    public void updateIssue(Issue existingIssue) {
        // Ensure proper bidirectional linkage for IssueCreators
        Set<IssueCreator> updatedIssueCreators = existingIssue.getIssueCreators();
        if (updatedIssueCreators != null) {
            for (IssueCreator ic : updatedIssueCreators) {
                ic.setIssue(existingIssue);
            }
        }

        // Ensure proper bidirectional linkage for Characters
        Set<ComicCharacter> updatedCharacters = existingIssue.getCharacters();
        if (updatedCharacters != null) {
            for (ComicCharacter character : updatedCharacters) {
                character.getIssues().add(existingIssue);
            }
        }

        // Update the issue in the database
        issueDao.save(existingIssue);
    }

    public void deleteIssue(Long issueId) {
        EntityTransaction transaction = null;
        try (EntityManager em = getEntityManager()) {
            transaction = em.getTransaction();
            transaction.begin();

            Issue issue = em.find(Issue.class, issueId);
            if (issue == null) {
                throw new IllegalArgumentException("Issue with ID " + issueId + " not found.");
            }
            System.out.println("Issue found: " + issue);

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
            System.out.println("Issue marked for deletion.");

            transaction.commit();
            System.out.println("Issue deleted from the database.");

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                System.err.println("Error deleting issue: " + e.getMessage());
            }
            throw e;
        }
    }

    public Issue getIssueByIdWithDetails(Long id) {
        return issueDao.findByIdWithDetails(id);
    }
}