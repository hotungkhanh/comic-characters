package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.IssueDaoImpl;
import com.tuka.comiccharacters.model.Issue;

public class IssueService extends AbstractService<Issue> {

    public IssueService() {
        super(new IssueDaoImpl());
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
