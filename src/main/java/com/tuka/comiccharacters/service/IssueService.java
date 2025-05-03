package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.IssueDaoImpl;
import com.tuka.comiccharacters.model.Issue;

import java.math.BigDecimal;

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

        BigDecimal issueNumber = issue.getIssueNumber();
        BigDecimal maxIssueNumber = new BigDecimal("999999.99");
        if (issueNumber.abs().compareTo(maxIssueNumber) > 0) {
            throw new IllegalArgumentException("Issue number must be less than or equal to 999999.99");
        }

        String overview = issue.getOverview();
        if (overview != null && overview.length() > 3000) {
            throw new IllegalArgumentException("Issue overview must be 3000 characters or fewer.");
        }

        if (issue.getPriceUsd() != null) {
            BigDecimal maxPrice = new BigDecimal("9999.99");
            if (issue.getPriceUsd().abs().compareTo(maxPrice) > 0) {
                throw new IllegalArgumentException("Price must be less than or equal to 9999.99");
            }
        }

        if (issue.getAnnual() == null) {
            throw new IllegalArgumentException("Annual flag cannot be null");
        }
    }
}
