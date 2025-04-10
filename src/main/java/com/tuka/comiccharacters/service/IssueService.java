package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.IssueDaoImpl;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.IssueCreator;
import com.tuka.comiccharacters.model.Series;

import java.util.HashSet;
import java.util.List;

public class IssueService {
    private final IssueDaoImpl issueDao = new IssueDaoImpl();

    public void addIssue(Series series, int number) {
        Issue issue = new Issue(series, number);
        issueDao.save(issue);
    }

    public void addIssue(Series series, int issueNumber, List<IssueCreator> issueCreators) {
        Issue issue = new Issue(series, issueNumber);

        for (IssueCreator ic : issueCreators) {
            ic.setIssue(issue);
        }

        issue.setIssueCreators(new HashSet<>(issueCreators));
        issueDao.save(issue);
    }

}
