package com.tuka.comiccharacters.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "characters")
public class ComicCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String alias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToMany
    @JoinTable(
            name = "character_creators",
            joinColumns = @JoinColumn(name = "character_id"),
            inverseJoinColumns = @JoinColumn(name = "creator_id")
    )
    private Set<Creator> creators = new HashSet<>();

    @ManyToMany(mappedBy = "characters", fetch = FetchType.EAGER)
    private Set<Issue> issues = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_appearance_issue_id")
    private Issue firstAppearance;

    public ComicCharacter() {
    }

    public ComicCharacter(String name, String alias, Publisher publisher) {
        this.name = name;
        this.alias = alias;
        this.publisher = publisher;
    }

    public ComicCharacter(String name, String alias, Publisher publisher, List<Creator> creatorList) {
        this.name = name;
        this.alias = alias;
        this.publisher = publisher;
        this.creators.addAll(creatorList);
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

    public Set<Creator> getCreators() {
        return creators;
    }

    public void setCreators(Set<Creator> creators) {
        this.creators = creators;
    }

    public Set<Issue> getIssues() {
        return issues;
    }

    public void setIssues(Set<Issue> issues) {
        this.issues = issues;
    }

    public void addCreator(Creator creator) {
        this.creators.add(creator);
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

