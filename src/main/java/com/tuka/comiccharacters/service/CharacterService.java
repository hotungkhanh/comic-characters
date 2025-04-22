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

    public void addCharacter(String name, String alias, Publisher publisher, String overview, List<Creator> creatorList, Issue firstAppearance) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Character name cannot be empty.");
        }
        ComicCharacter comicCharacter = new ComicCharacter(name.trim(), alias != null ? alias.trim() : null, publisher, overview);
        comicCharacter.setFirstAppearance(firstAppearance);
        if (creatorList != null && !creatorList.isEmpty()) {
            comicCharacter.getCreators().addAll(creatorList);
        }
        characterDao.save(comicCharacter);
    }

    public Set<ComicCharacter> getAllCharacters() {
        return characterDao.findAll();
    }

    public ComicCharacter getCharacterByIdWithDetails(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid character ID.");
        }
        return characterDao.findByIdWithDetails(id);
    }

    public void updateCharacter(ComicCharacter character) {
        if (character == null || character.getId() == null || character.getId() <= 0) {
            throw new IllegalArgumentException("Invalid character object for update.");
        }
        if (character.getName() == null || character.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Character name cannot be empty.");
        }
        character.setName(character.getName().trim());
        if (character.getAlias() != null) {
            character.setAlias(character.getAlias().trim());
        }
        characterDao.save(character);
    }

    public void deleteCharacter(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid character ID for deletion.");
        }
        ComicCharacter character = characterDao.findById(id);
        if (character != null) {
            characterDao.delete(character);
        }
    }
}