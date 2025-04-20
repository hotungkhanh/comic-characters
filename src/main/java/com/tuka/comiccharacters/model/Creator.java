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

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<IssueCreator> issueCreators = new HashSet<>();

    @ManyToMany(mappedBy = "creators")
    private Set<ComicCharacter> creditedCharacters = new HashSet<>();

    public Creator() {
    }

    public Creator(String name) {
        this.name = name;
    }

    public Creator(String name, String overview) {
        this.name = name;
        this.overview = overview;
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

    public Set<IssueCreator> getIssueCreators() {
        return issueCreators;
    }

    public void setIssueCreators(Set<IssueCreator> issueCreators) {
        this.issueCreators = issueCreators;
    }

    public Set<ComicCharacter> getCreditedCharacters() {
        return creditedCharacters;
    }

    public void setCreditedCharacters(Set<ComicCharacter> creditedCharacters) {
        this.creditedCharacters = creditedCharacters;
    }

    @Override
    public String toString() {
        return name;
    }
}
