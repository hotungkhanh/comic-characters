package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.CharacterDao;
import com.tuka.comiccharacters.model.Character;

public class CharacterService {
    private final CharacterDao characterDao = new CharacterDao();

    public void addCharacter(String name, String alias, String publisher) {
        Character character = new Character(name, alias, publisher);
        characterDao.save(character);
    }
}
