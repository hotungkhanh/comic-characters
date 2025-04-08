package com.tuka.comiccharacters.model;

import jakarta.persistence.*;

@Entity
@Table(name = "issues")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String series;
    private int issueNumber;

    public Issue() {
    }

    public Issue(String series, int issueNumber) {
        this.series = series;
        this.issueNumber = issueNumber;
    }

    public int getId() {
        return id;
    }

    public String getSeries() {
        return series;
    }

    public int getIssueNumber() {
        return issueNumber;
    }
}
