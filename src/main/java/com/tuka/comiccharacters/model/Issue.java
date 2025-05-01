package com.tuka.comiccharacters.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "issues")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "series_id", nullable = false)
    private Series series;

    @Column(precision = 6, scale = 2, nullable = false)
    private BigDecimal issueNumber;

    @Column(length = 1000)
    private String overview;

    @Column(length = 2083)
    private String imageUrl;

    private LocalDate releaseDate;

    @Column(precision = 6, scale = 2)
    private BigDecimal priceUsd;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<IssueCreator> issueCreators = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "issue_characters", joinColumns = @JoinColumn(name = "issue_id"), inverseJoinColumns = @JoinColumn(name = "character_id"))
    private Set<ComicCharacter> characters = new HashSet<>();

    @Column(nullable = false)
    private Boolean isAnnual = false;

    public Issue() {
    }

    public Issue(Series series, BigDecimal issueNumber) {
        this.series = series;
        this.issueNumber = issueNumber;
    }

    public Issue(Series series, BigDecimal issueNumber, List<IssueCreator> issueCreators) {
        this.series = series;
        this.issueNumber = issueNumber;
        this.issueCreators.addAll(issueCreators);
    }

    public Issue(Series series, BigDecimal issueNumber, LocalDate releaseDate) {
        this.series = series;
        this.issueNumber = issueNumber;
        this.releaseDate = releaseDate;
    }

    public Long getId() {
        return id;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public BigDecimal getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(BigDecimal issueNumber) {
        this.issueNumber = issueNumber;
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

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(BigDecimal priceUsd) {
        this.priceUsd = priceUsd;
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

    public Boolean getAnnual() {
        return isAnnual;
    }

    public void setAnnual(Boolean annual) {
        isAnnual = annual;
    }

    @Override
    public String toString() {
        String issueNumberStr;
        if (issueNumber != null) {
            issueNumberStr = issueNumber.stripTrailingZeros().toPlainString();
        } else {
            issueNumberStr = "";
        }

        if (isAnnual) {
            return series + " Annual #" + issueNumberStr;
        } else {
            return series + " #" + issueNumberStr;
        }
    }
}
