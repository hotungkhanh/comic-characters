package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.ui.details.CreatorDetails;
import com.tuka.comiccharacters.ui.form.CreatorForm;

import javax.swing.*;
import java.util.Comparator;

public class CreatorBrowser extends AbstractBrowser<Creator, CreatorService> {

    public CreatorBrowser(JFrame parentFrame) {
        super("Creators", parentFrame, new CreatorService());
    }

    @Override
    protected boolean matchesQuery(Creator creator, String query) {
        return creator.getName().toLowerCase().contains(query.toLowerCase());
    }

    @Override
    protected Comparator<Creator> getComparator() {
        return Comparator.comparing(c -> c.getName().toLowerCase());
    }

    @Override
    protected void showDetails(Creator creator) {
        Creator fullCreator = service.getByIdWithDetails(creator.getId());
        new CreatorDetails(this, fullCreator, this::refreshEntities).showDetailsDialog();
    }

    @Override
    protected void showAddForm() {
        JDialog dialog = new JDialog(parentFrame, "Add New Creator", true);
        dialog.setContentPane(new CreatorForm());
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
        refreshEntities();
    }
}
