package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.service.CharacterService;
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
    protected void showDetails(ComicCharacter character) {
        ComicCharacter fullCharacter = service.getByIdWithDetails(character.getId());
        new CharacterDetails(this, fullCharacter, this::refreshEntities).showDetailsDialog();
    }

    @Override
    protected void showAddForm() {
        JDialog dialog = new JDialog(parentFrame, "Add New Character", true);
        dialog.setContentPane(new CharacterForm());
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
        refreshEntities();
    }
}
