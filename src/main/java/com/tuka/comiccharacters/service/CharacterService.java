package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.ComicCharacterDaoImpl;
import com.tuka.comiccharacters.model.ComicCharacter;

public class CharacterService {
    private final ComicCharacterDaoImpl characterDao = new ComicCharacterDaoImpl();

    public void addCharacter(String name, String alias, String publisher) {
        ComicCharacter comicCharacter = new ComicCharacter(name, alias, publisher);
        characterDao.save(comicCharacter);
    }
}
