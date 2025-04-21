package com.tuka.comiccharacters.ui.browser;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractBrowserPanel<T> extends JPanel {

    protected final DefaultListModel<T> listModel = new DefaultListModel<>();
    protected final JList<T> entityList = new JList<>(listModel);
    protected final List<T> allEntities = new ArrayList<>();

    public AbstractBrowserPanel(String title) {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder(title));

        entityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(entityList);
        add(scrollPane, BorderLayout.CENTER);

        entityList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    T selected = entityList.getSelectedValue();
                    if (selected != null) {
                        showDetails(selected);
                    }
                }
            }
        });

    }

    public void refreshEntities() {
        listModel.clear();
        allEntities.clear();

        List<T> entities = new ArrayList<>(getEntities());
        entities.sort(getComparator());
        allEntities.addAll(entities);

        for (T entity : entities) {
            listModel.addElement(entity);
        }
    }

    public void filter(String query) {
        listModel.clear();
        for (T entity : allEntities) {
            if (matchesQuery(entity, query)) {
                listModel.addElement(entity);
            }
        }
    }

    protected abstract Collection<T> getEntities();

    protected abstract boolean matchesQuery(T entity, String query);

    protected abstract Comparator<T> getComparator();

    protected abstract void showDetails(T entity);
}
