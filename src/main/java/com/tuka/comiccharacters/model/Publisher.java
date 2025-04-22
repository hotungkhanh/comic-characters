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

    @OneToMany(mappedBy = "publisher")
    private Set<Series> publisherSeries = new HashSet<>();

    @OneToMany(mappedBy = "publisher")
    private final Set<ComicCharacter> publisherCharacters = new HashSet<>();

    public Publisher() {
    }

    public Publisher(String name) {
        this.name = name;
    }

    public Publisher(String name, Set<Series> seriesList) {
        this.name = name;
        this.publisherSeries = seriesList;
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

    public Set<Series> getPublisherSeries() {
        return publisherSeries;
    }

    public Set<ComicCharacter> getPublisherCharacters() {
        return publisherCharacters;
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
