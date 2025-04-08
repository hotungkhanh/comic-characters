package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.ComicCharacter;

import java.util.List;

public interface ComicCharacterDao {
    void save(ComicCharacter character);

    ComicCharacter findById(Long id);

    List<ComicCharacter> findAll();

    void deleteById(Long id);
}
