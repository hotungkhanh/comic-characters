package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.ComicCharacter;

public class ComicCharacterDaoImpl extends AbstractJpaDao<ComicCharacter> {
    public ComicCharacterDaoImpl() {
        super(ComicCharacter.class);
    }
}
