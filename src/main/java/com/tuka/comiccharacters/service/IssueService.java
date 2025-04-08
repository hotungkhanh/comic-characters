package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.IssueDaoImpl;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Series;

public class IssueService {
    private final IssueDaoImpl issueDao = new IssueDaoImpl();

    public void addIssue(Series series, int number) {
        Issue issue = new Issue(series, number);
        issueDao.save(issue);
    }
}
