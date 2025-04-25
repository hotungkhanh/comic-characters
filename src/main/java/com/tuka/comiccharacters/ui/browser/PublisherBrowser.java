package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.ui.details.AbstractDetails;
import com.tuka.comiccharacters.ui.details.PublisherDetails;
import com.tuka.comiccharacters.ui.form.PublisherForm;

import javax.swing.*;
import java.util.Comparator;

public class PublisherBrowser extends AbstractBrowser<Publisher, PublisherService> {

    public PublisherBrowser(JFrame parentFrame) {
        super("Publishers", parentFrame, new PublisherService());
    }

    @Override
    protected boolean matchesQuery(Publisher publisher, String query) {
        return matchesNameField(publisher.getName(), query);
    }

    @Override
    protected Comparator<Publisher> getComparator() {
        return Comparator.comparing(Publisher::getName);
    }

    @Override
    protected Long getEntityId(Publisher entity) {
        return entity.getId();
    }

    @Override
    protected JComponent createForm() {
        return new PublisherForm();
    }

    @Override
    protected AbstractDetails<Publisher> createDetailsDialog(Publisher publisher, Runnable refreshCallback) {
        return new PublisherDetails(this, publisher, refreshCallback);
    }
}
