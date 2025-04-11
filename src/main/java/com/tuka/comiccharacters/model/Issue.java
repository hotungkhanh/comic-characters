package com.tuka.comiccharacters.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<IssueCreator> issueCreators = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "issue_characters",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "character_id")
    )
    private Set<ComicCharacter> characters = new HashSet<>();

    public Issue() {
    }

    public Issue(Series series, int issueNumber) {
        this.series = series;
        this.issueNumber = issueNumber;
    }

    public Issue(Series series, int issueNumber, List<IssueCreator> issueCreators) {
        this.series = series;
        this.issueNumber = issueNumber;
        this.issueCreators.addAll(issueCreators);
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

    public Set<IssueCreator> getIssueCreators() {
        return issueCreators;
    }

    public void setIssueCreators(Set<IssueCreator> issueCreators) {
        this.issueCreators = issueCreators;
    }

    public Set<ComicCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(Set<ComicCharacter> characters) {
        this.characters = characters;
    }

    public void addCharacter(ComicCharacter character) {
        characters.add(character);
        character.getIssues().add(this);
    }

    public void removeCharacter(ComicCharacter character) {
        characters.remove(character);
        character.getIssues().remove(this);
    }

    @Override
    public String toString() {
        return series + " #" + issueNumber;
    }
}

