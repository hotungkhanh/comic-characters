package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Issue;

public class IssueDaoImpl extends AbstractJpaDao<Issue> {
    public IssueDaoImpl() {
        super(Issue.class);
    }
}
