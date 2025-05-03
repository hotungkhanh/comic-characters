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

        String name = character.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Character name cannot be empty.");
        }
        name = name.trim();
        if (name.length() > 255) {
            throw new IllegalArgumentException("Character name must be 255 characters or fewer.");
        }
        character.setName(name);

        String alias = character.getAlias();
        if (alias != null && alias.length() > 255) {
            throw new IllegalArgumentException("Character alias must be 255 characters or fewer.");
        }

        String overview = character.getOverview();
        if (overview != null && overview.length() > 3000) {
            throw new IllegalArgumentException("Character overview must be 3000 characters or fewer.");
        }

        String imageUrl = character.getImageUrl();
        if (imageUrl != null && imageUrl.length() > 2083) {
            throw new IllegalArgumentException("Character image URL must be 2083 characters or fewer.");
        }
    }
}
