package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.CharacterDaoImpl;
import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Publisher;

public class CharacterService {
    private final CharacterDaoImpl characterDao = new CharacterDaoImpl();

    public void addCharacter(String name, String alias, Publisher publisher) {
        ComicCharacter comicCharacter = new ComicCharacter(name, alias, publisher);
        characterDao.save(comicCharacter);
    }
}
