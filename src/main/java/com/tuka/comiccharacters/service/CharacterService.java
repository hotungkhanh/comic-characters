package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.CharacterDaoImpl;
import com.tuka.comiccharacters.model.ComicCharacter;

public class CharacterService extends AbstractService<ComicCharacter> {

    public CharacterService() {
        super(new CharacterDaoImpl());
    }

    @Override
    protected void validateEntity(ComicCharacter character) {
        if (character == null) {
            throw new IllegalArgumentException("Character cannot be null.");
        }
        if (character.getId() != null && character.getId() <= 0) {
            throw new IllegalArgumentException("Invalid character ID.");
        }
        if (character.getName() == null || character.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Character name cannot be empty.");
        }
    }
}
