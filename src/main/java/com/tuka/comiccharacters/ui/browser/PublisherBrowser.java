package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.ui.details.PublisherDetails;
import com.tuka.comiccharacters.ui.form.PublisherForm;

import javax.swing.*;
import java.util.Collection;
import java.util.Comparator;

public class PublisherBrowser extends AbstractBrowser<Publisher> {
    private final PublisherService publisherService;

    public PublisherBrowser(JFrame parentFrame) {
        super("Publishers", parentFrame);
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
        return Comparator.comparing(Publisher::getName);
    }

    @Override
    protected void showDetails(Publisher publisher) {
        Publisher fullPublisher = publisherService.getByIdWithDetails(publisher.getId());
        new PublisherDetails(this, fullPublisher, this::refreshEntities).showDetailsDialog();
    }

    @Override
    protected void showAddForm() {
        JDialog dialog = new JDialog(parentFrame, "Add New Publisher", true);
        dialog.setContentPane(new PublisherForm());
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
        refreshEntities();
    }
}
