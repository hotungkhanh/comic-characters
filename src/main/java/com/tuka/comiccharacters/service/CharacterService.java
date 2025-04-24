package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.CharacterDaoImpl;
import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Publisher;

import java.util.List;
import java.util.Set;

public class CharacterService extends AbstractService<ComicCharacter> {

    public CharacterService() {
        super(new CharacterDaoImpl());
    }

    public void addCharacter(String name, String alias, Publisher publisher, String overview,
                             List<Creator> creatorList, Issue firstAppearance) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Character name cannot be empty.");
        }
        ComicCharacter comicCharacter = new ComicCharacter(name.trim(),
                alias != null ? alias.trim() : null,
                publisher,
                overview);
        comicCharacter.setFirstAppearance(firstAppearance);
        if (creatorList != null && !creatorList.isEmpty()) {
            comicCharacter.getCreators().addAll(creatorList);
        }
        save(comicCharacter);
    }

    public Set<ComicCharacter> getAllCharacters() {
        return dao.findAll();
    }

    public ComicCharacter getCharacterByIdWithDetails(Long id) {
        return findByIdWithDetails(id);
    }

    public void updateCharacter(ComicCharacter character) {
        validateEntity(character);

        // Extra character-specific processing
        character.setName(character.getName().trim());
        if (character.getAlias() != null) {
            character.setAlias(character.getAlias().trim());
        }

        save(character);
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
