package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.service.PublisherService;
//import com.tuka.comiccharacters.ui.details.PublisherDetails;

import java.util.Collection;
import java.util.Comparator;

public class PublisherBrowser extends AbstractBrowser<Publisher> {

    private final PublisherService publisherService;

    public PublisherBrowser() {
        super("Publishers");
        this.publisherService = new PublisherService();
        refreshEntities();
    }

    @Override
    protected Collection<Publisher> getEntities() {
        return publisherService.getAllPublishers();
    }

    @Override
    protected boolean matchesQuery(Publisher publisher, String query) {
        return publisher.getName().toLowerCase().contains(query.toLowerCase());
    }

    @Override
    protected Comparator<Publisher> getComparator() {
        return Comparator.comparing(p -> p.getName().toLowerCase());
    }

    @Override
    protected void showDetails(Publisher publisher) {
//        new PublisherDetails(this, publisher, this::refreshEntities).showDetailsDialog();
    }
}