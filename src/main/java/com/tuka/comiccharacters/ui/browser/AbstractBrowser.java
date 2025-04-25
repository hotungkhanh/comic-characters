package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.service.AbstractService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractBrowser<T, S extends AbstractService<T>> extends JPanel {
    protected final DefaultListModel<T> listModel = new DefaultListModel<>();
    protected final JList<T> entityList = new JList<>(listModel);
    protected final List<T> allEntities = new ArrayList<>();
    protected final JFrame parentFrame;
    protected final S service; // New generic service field

    public AbstractBrowser(String title, JFrame parentFrame, S service) {
        super(new BorderLayout(5, 5));
        this.parentFrame = parentFrame;
        this.service = service;

        // Title label
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Entity list with scrolling
        JScrollPane scrollPane = new JScrollPane(entityList);
        add(scrollPane, BorderLayout.CENTER);

        // Add New button at the bottom
        JButton addButton = new JButton("Add New " + title);
        addButton.addActionListener(e -> showAddForm());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // List selection listener
        entityList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && entityList.getSelectedValue() != null) {
                showDetails(entityList.getSelectedValue());
            }
        });

        // Refresh entities at initialization
        refreshEntities();
    }

    protected Collection<T> getEntities() {
        return service.getAllEntities();
    }

    public void refreshEntities() {
        allEntities.clear();
        allEntities.addAll(getEntities());
        allEntities.sort(getComparator());
        updateListModel();
    }

    public void filter(String query) {
        updateListModel(query);
    }

    private void updateListModel() {
        updateListModel("");
    }

    private void updateListModel(String query) {
        listModel.clear();
        for (T entity : allEntities) {
            if (query.isEmpty() || matchesQuery(entity, query)) {
                listModel.addElement(entity);
            }
        }
    }

    protected abstract boolean matchesQuery(T entity, String query);
    protected abstract Comparator<T> getComparator();
    protected abstract void showDetails(T entity);
    protected abstract void showAddForm();
}