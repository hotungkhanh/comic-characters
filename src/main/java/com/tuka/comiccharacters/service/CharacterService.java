package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.CharacterDaoImpl;
import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Publisher;

import java.util.HashSet;
import java.util.List;

public class CharacterService extends AbstractService<ComicCharacter> {

    public CharacterService() {
        super(new CharacterDaoImpl());
    }

    /**
     * Adds a new character to the database
     *
     * @param character The character to add
     */
    public void addCharacter(ComicCharacter character) {
        validateEntity(character);
        save(character);
    }

    /**
     * Adds a new character to the database
     *
     * @param name            The name of the character
     * @param alias           The alias of the character
     * @param publisher       The publisher of the character
     * @param overview        The overview/description of the character
     * @param creators        The creators associated with the character
     * @param firstAppearance The issue in which the character first appeared
     */
    public void addCharacter(String name, String alias, Publisher publisher, String overview, List<Creator> creators, Issue firstAppearance) {
        ComicCharacter character = new ComicCharacter(name, alias, publisher, overview);
        character.setCreators(new HashSet<>(creators));
        character.setFirstAppearance(firstAppearance);
        save(character);
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

    @Override
    public void delete(Long id) {
        validateId(id);

        try {
            executeInTransaction(em -> {
                ComicCharacter character = em.find(ComicCharacter.class, id);
                if (character == null) {
                    throw new IllegalArgumentException("Character with ID " + id + " not found.");
                }

                // Remove this character from any issues it appears in
                for (Issue issue : new HashSet<>(character.getIssues())) {
                    issue.getCharacters().remove(character);
                }
                character.getIssues().clear();

                // Remove this character from any creators it's associated with
                for (Creator creator : new HashSet<>(character.getCreators())) {
                    creator.getCreditedCharacters().remove(character);
                }
                character.getCreators().clear();

                // Finally, delete the character
                em.remove(character);
            });
        } catch (Exception e) {
            throw new RuntimeException("Error deleting character with ID " + id, e);
        }
    }
}
