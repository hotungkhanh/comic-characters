package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.service.CharacterService;

import java.util.Collection;
import java.util.Comparator;

public class CharacterBrowser extends AbstractBrowser<ComicCharacter> {

    private final CharacterService characterService;

    public CharacterBrowser() {
        super("Characters");
        this.characterService = new CharacterService();
        refreshEntities();
    }

    @Override
    protected Collection<ComicCharacter> getEntities() {
        return characterService.getAllCharacters();
    }

    @Override
    protected boolean matchesQuery(ComicCharacter character, String query) {
        return character.getName().toLowerCase().contains(query.toLowerCase()) ||
                (character.getAlias() != null && character.getAlias().toLowerCase().contains(query.toLowerCase()));
    }

    @Override
    protected Comparator<ComicCharacter> getComparator() {
        return Comparator.comparing(ComicCharacter::getName, String::compareToIgnoreCase);
    }

    @Override
    protected void showDetails(ComicCharacter character) {
//        new CharacterDetails(this, character, this::refreshEntities).showDetailsDialog();
    }
}