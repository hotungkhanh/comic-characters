package com.tuka.comiccharacters.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "series")
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private int startYear;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL)
    private List<Issue> issues = new ArrayList<>();

    public Series() {
    }

    public Series(String title, int startYear) {
        this.title = title;
        this.startYear = startYear;
    }

    @Override
    public String toString() {
        return title + " (" + startYear + ")";
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }
}
