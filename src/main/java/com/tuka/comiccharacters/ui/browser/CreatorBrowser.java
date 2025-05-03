package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.ui.details.AbstractDetails;
import com.tuka.comiccharacters.ui.details.CreatorDetails;
import com.tuka.comiccharacters.ui.form.CreatorForm;

import javax.swing.*;

public class CreatorBrowser extends AbstractBrowser<Creator, CreatorService> {

    public CreatorBrowser(JFrame parentFrame) {
        super("Creators", parentFrame, new CreatorService());
    }

    @Override
    protected boolean matchesQuery(Creator creator, String query) {
        return matchesNameField(creator.getName(), query);
    }

    @Override
    protected Long getEntityId(Creator creator) {
        return creator.getId();
    }

    @Override
    protected JComponent createForm() {
        return new CreatorForm();
    }

    @Override
    protected AbstractDetails<Creator> createDetailsDialog(Creator creator, Runnable refreshCallback) {
        return new CreatorDetails(this, creator, refreshCallback);
    }
}
