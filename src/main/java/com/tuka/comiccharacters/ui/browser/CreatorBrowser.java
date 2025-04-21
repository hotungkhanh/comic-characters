package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.ui.details.CreatorDetails;

import java.util.Collection;
import java.util.Comparator;

public class CreatorBrowser extends AbstractBrowser<Creator> {

    private final CreatorService creatorService;

    public CreatorBrowser() {
        super("Creators");
        this.creatorService = new CreatorService();
        refreshEntities();
    }

    @Override
    protected Collection<Creator> getEntities() {
        return creatorService.getAllCreators();
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
        Creator fullCreator = creatorService.getCreatorByIdWithDetails(creator.getId());
        new CreatorDetails(this, fullCreator, this::refreshEntities).showDetailsDialog();
    }
}
