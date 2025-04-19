package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.service.CreatorService;
import com.tuka.comiccharacters.ui.details.CreatorDetails;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CreatorBrowser extends JPanel {

    private final CreatorService creatorService;
    private final DefaultListModel<Creator> listModel;
    private final List<Creator> allCreators; // Full list for filtering

    public CreatorBrowser() {
        this.creatorService = new CreatorService();
        this.listModel = new DefaultListModel<>();
        this.allCreators = new ArrayList<>();

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Creators"));

        // Creator list
        JList<Creator> creatorList = new JList<>(listModel);
        creatorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(creatorList);
        add(scrollPane, BorderLayout.CENTER);

        // Double-click to view details
        creatorList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    Creator selected = creatorList.getSelectedValue();
                    if (selected != null) {
                        CreatorDetails.show(CreatorBrowser.this, selected, CreatorBrowser.this::refreshCreators);
                    }
                }
            }
        });

        refreshCreators();
    }

    public void refreshCreators() {
        listModel.clear();
        allCreators.clear();

        List<Creator> creators = creatorService.getAllCreators();
        creators.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        allCreators.addAll(creators);

        for (Creator creator : creators) {
            listModel.addElement(creator);
        }
    }

    public void filter(String query) {
        listModel.clear();
        for (Creator creator : allCreators) {
            if (creator.getName().toLowerCase().contains(query.toLowerCase())) {
                listModel.addElement(creator);
            }
        }
    }
}

