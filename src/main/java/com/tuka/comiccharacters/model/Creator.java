package com.tuka.comiccharacters.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "creators")
public class Creator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String overview;

    @Column(length = 2083)
    private String imageUrl;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<IssueCreator> issueCreators = new HashSet<>();

    @ManyToMany(mappedBy = "creators")
    private final Set<ComicCharacter> creditedCharacters = new HashSet<>();

    public Creator() {
    }

    public Creator(String name) {
        this.name = name;
    }

    public Creator(String name, String overview) {
        this.name = name;
        this.overview = overview;
    }

    public Creator(String name, String overview, String imageUrl) {
        this.name = name;
        this.overview = overview;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<IssueCreator> getIssueCreators() {
        return issueCreators;
    }

    public Set<ComicCharacter> getCreditedCharacters() {
        return creditedCharacters;
    }

    @Override
    public String toString() {
        return name;
    }
}
