package com.tuka.comiccharacters.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "publishers")
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Series> allSeries = new HashSet<>();

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ComicCharacter> allCharacters = new HashSet<>();

    public Publisher() {
    }

    public Publisher(String name) {
        this.name = name;
    }

    public Publisher(String name, Set<Series> seriesList) {
        this.name = name;
        this.allSeries = seriesList;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Series> getAllSeries() {
        return allSeries;
    }

    public void setAllSeries(Set<Series> allSeries) {
        this.allSeries = allSeries;
    }

    public void addSeries(Series series) {
        allSeries.add(series);
        series.setPublisher(this);
    }

    public void removeSeries(Series series) {
        allSeries.remove(series);
        series.setPublisher(null);
    }

    public Set<ComicCharacter> getAllCharacters() {
        return allCharacters;
    }

    public void setAllCharacters(Set<ComicCharacter> allCharacters) {
        this.allCharacters = allCharacters;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Publisher other = (Publisher) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
