package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.service.CharacterService;
import com.tuka.comiccharacters.ui.details.AbstractDetails;
import com.tuka.comiccharacters.ui.details.CharacterDetails;
import com.tuka.comiccharacters.ui.form.CharacterForm;

import javax.swing.*;
import java.util.Comparator;

public class CharacterBrowser extends AbstractBrowser<ComicCharacter, CharacterService> {

    public CharacterBrowser(JFrame parentFrame) {
        super("Characters", parentFrame, new CharacterService());
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
    protected Long getEntityId(ComicCharacter entity) {
        return entity.getId();
    }

    @Override
    protected JComponent createForm() {
        return new CharacterForm();
    }

    @Override
    protected AbstractDetails<ComicCharacter> createDetailsDialog(ComicCharacter entity, Runnable refreshCallback) {
        return new CharacterDetails(this, entity, refreshCallback);
    }
}
