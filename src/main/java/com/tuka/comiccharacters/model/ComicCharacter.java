package com.tuka.comiccharacters.model;

import jakarta.persistence.*;

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
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    public ComicCharacter() {}

    public ComicCharacter(String name, String alias, Publisher publisher) {
        this.name = name;
        this.alias = alias;
        this.publisher = publisher;
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

    @Override
    public String toString() {
        return "Character{id=" + id + ", name='" + name + "', alias='" + alias +
                "', publisher=" + (publisher != null ? publisher.getName() : "null") + "}";
    }
}
