package com.tuka.comiccharacters.model;

import jakarta.persistence.*;

@Entity
@Table(name = "issues")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "series_id", nullable = false)
    private Series series;

    private int issueNumber;

    public Issue() {
    }

    public Issue(Series series, int issueNumber) {
        this.series = series;
        this.issueNumber = issueNumber;
    }

    public int getId() {
        return id;
    }

    public Series getSeries() {
        return series;
    }

    public int getIssueNumber() {
        return issueNumber;
    }
}
