package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.IssueDaoImpl;
import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.IssueCreator;
import com.tuka.comiccharacters.model.Series;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IssueService {
    private final IssueDaoImpl issueDao = new IssueDaoImpl();

    public void addIssue(Series series, BigDecimal issueNumber, List<IssueCreator> issueCreators, List<ComicCharacter> characters) {
        Issue issue = new Issue(series, issueNumber);

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
}
