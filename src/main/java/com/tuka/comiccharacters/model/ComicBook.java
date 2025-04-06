package com.tuka.comiccharacters.model;

import jakarta.persistence.*;

@Entity
@Table(name = "comic_books")
public class ComicBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private int year;

    public ComicBook(String batman, int i) {
        this.title = batman;
        this.year = i;
    }

    public ComicBook() {

    }

    // Constructors, getters, setters
}
