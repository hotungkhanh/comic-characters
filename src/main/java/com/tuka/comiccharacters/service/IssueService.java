package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.IssueDaoImpl;
import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.IssueCreator;
import com.tuka.comiccharacters.model.Series;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IssueService extends AbstractService<Issue> {

    public IssueService() {
        super(new IssueDaoImpl());
    }

    public void saveIssue(Issue issue, List<IssueCreator> issueCreators, List<ComicCharacter> characters) {
        try {
            executeInTransaction(em -> {
                setIssueCreators(issue, issueCreators);
                setIssueCharacters(em, issue, characters);
                save(issue);
            });
        } catch (Exception e) {
            throw new RuntimeException("Error saving issue: " + e.getMessage(), e);
        }
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
