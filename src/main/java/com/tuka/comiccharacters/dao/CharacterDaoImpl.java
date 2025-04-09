package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.ComicCharacter;

public class CharacterDaoImpl extends AbstractJpaDao<ComicCharacter> {
    public CharacterDaoImpl() {
        super(ComicCharacter.class);
    }
}
