package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Creator;

public class CreatorDaoImpl extends AbstractJpaDao<Creator> {
    public CreatorDaoImpl() {
        super(Creator.class);
    }
}
