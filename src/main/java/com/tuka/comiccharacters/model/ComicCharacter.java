package com.tuka.comiccharacters.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "characters")
public class ComicCharacter {

    @ManyToMany(mappedBy = "characters", fetch = FetchType.LAZY)
    private final Set<Issue> issues = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String alias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @Column(length = 1000)
    private String overview;

    @Column(length = 2083)
    private String imageUrl;

    @ManyToMany
    @JoinTable(name = "character_creators", joinColumns = @JoinColumn(name = "character_id"), inverseJoinColumns = @JoinColumn(name = "creator_id"))
    private Set<Creator> creators = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_appearance_issue_id")
    private Issue firstAppearance;

    public ComicCharacter() {
    }

    public ComicCharacter(String name) {
        this.name = name;
    }

    public ComicCharacter(String name, String alias, Publisher publisher, String overview) {
        this.name = name;
        this.alias = alias;
        this.publisher = publisher;
        this.overview = overview;
    }

    public ComicCharacter(String name, String alias, Publisher publisher, String overview, String imageUrl) {
        this.name = name;
        this.alias = alias;
        this.publisher = publisher;
        this.overview = overview;
        this.imageUrl = imageUrl;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
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

    public Set<Creator> getCreators() {
        return creators;
    }

    public void setCreators(Set<Creator> creators) {
        this.creators = creators;
    }

    public Set<Issue> getIssues() {
        return issues;
    }

    public Issue getFirstAppearance() {
        return firstAppearance;
    }

    public void setFirstAppearance(Issue firstAppearance) {
        this.firstAppearance = firstAppearance;
    }

    @Override
    public String toString() {
        if (alias == null || alias.isEmpty()) {
            return name;
        }
        return name + " (" + alias + ")";
    }
}
