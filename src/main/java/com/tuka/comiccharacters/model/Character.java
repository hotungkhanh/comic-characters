package com.tuka.comiccharacters.model;

import jakarta.persistence.*;

@Entity
@Table(name = "characters")
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String alias;

    private String publisher;

    public Character() {}

    public Character(String name, String alias, String publisher) {
        this.name = name;
        this.alias = alias;
        this.publisher = publisher;
    }

    // Getters and Setters

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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return "Character{id=" + id + ", name='" + name + "', alias='" + alias + "', publisher='" + publisher + "'}";
    }
}
