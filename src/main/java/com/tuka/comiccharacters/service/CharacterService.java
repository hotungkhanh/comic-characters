package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.CharacterDaoImpl;
import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Publisher;

import java.util.List;
import java.util.Set;

public class CharacterService {
    private final CharacterDaoImpl characterDao = new CharacterDaoImpl();

    // First overload: without creators, with optional first appearance
    public void addCharacter(String name, String alias, Publisher publisher, Issue firstAppearance) {
        ComicCharacter comicCharacter = new ComicCharacter(name, alias, publisher);
        comicCharacter.setFirstAppearance(firstAppearance); // nullable
        characterDao.save(comicCharacter);
    }

    // Second overload: with creators, with optional first appearance
    public void addCharacter(String name, String alias, Publisher publisher, List<Creator> creatorList, Issue firstAppearance) {
        ComicCharacter comicCharacter = new ComicCharacter(name, alias, publisher, creatorList);
        comicCharacter.setFirstAppearance(firstAppearance); // nullable
        characterDao.save(comicCharacter);
    }

    public Set<ComicCharacter> getAllCharacters() {
        return characterDao.findAll();
    }
}
