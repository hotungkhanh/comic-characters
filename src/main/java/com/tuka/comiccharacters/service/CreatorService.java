package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.CreatorDaoImpl;
import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Creator;

import java.util.HashSet;
import java.util.Set;

public class CreatorService extends AbstractService<Creator> {

    public CreatorService() {
        super(new CreatorDaoImpl());
    }

    /**
     * Adds a new creator with name and overview
     *
     * @param name     The name of the creator
     * @param overview The overview/description of the creator
     */
    public void addCreator(String name, String overview) {
        Creator creator = new Creator(name, overview);
        save(creator);
    }

    /**
     * Adds a new creator with name, overview and image URL
     *
     * @param name     The name of the creator
     * @param overview The overview/description of the creator
     * @param imageUrl The URL to the creator's image
     */
    public void addCreator(String name, String overview, String imageUrl) {
        Creator creator = new Creator(name, overview);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            creator.setImageUrl(imageUrl);
        }
        save(creator);
    }

    public void updateCreator(Creator creator) {
        save(creator);
    }

    @Override
    public void delete(Long id) {
        validateId(id);

        try {
            executeInTransaction(em -> {
                Creator creator = em.find(Creator.class, id);
                if (creator != null) {
                    // Remove the association with credited characters
                    Set<ComicCharacter> creditedCharacters = creator.getCreditedCharacters();
                    // To avoid ConcurrentModificationException, iterate over a copy
                    for (ComicCharacter character : new HashSet<>(creditedCharacters)) {
                        character.getCreators().remove(creator);
                    }
                    creator.getCreditedCharacters().clear(); // Clear the set on the Creator

                    em.remove(creator);
                } else {
                    throw new IllegalArgumentException("Creator with ID " + id + " not found.");
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Error deleting creator with ID " + id, e);
        }
    }

    @Override
    protected void validateEntity(Creator creator) {
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }
        if (creator.getName() == null || creator.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Creator name cannot be empty");
        }
    }
}
