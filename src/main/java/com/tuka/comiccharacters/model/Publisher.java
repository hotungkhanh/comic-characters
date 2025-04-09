package com.tuka.comiccharacters.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "publishers")
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Series> seriesList = new ArrayList<>();

    public Publisher() {
    }

    public Publisher(String name) {
        this.name = name;
    }

    public Publisher(String name, List<Series> seriesList) {
        this.name = name;
        this.seriesList = seriesList;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Series> getSeriesList() {
        return seriesList;
    }

    public void addSeries(Series series) {
        seriesList.add(series);
        series.setPublisher(this);
    }

    public void removeSeries(Series series) {
        seriesList.remove(series);
        series.setPublisher(null);
    }

    @Override
    public String toString() {
        return name;
    }
}
