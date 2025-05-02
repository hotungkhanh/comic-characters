package com.tuka.comiccharacters.ui.browser;

import com.tuka.comiccharacters.service.AbstractService;
import com.tuka.comiccharacters.ui.details.AbstractDetails;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractBrowser<T, S extends AbstractService<T>> extends JPanel {
    protected final DefaultListModel<T> listModel = new DefaultListModel<>();
    protected final JList<T> entityList = new JList<>(listModel);
    protected final List<T> allEntities = new ArrayList<>();
    protected final JFrame parentFrame;
    protected final S service;
    protected final String entityTypeName;

    public AbstractBrowser(String entityTypeName, JFrame parentFrame, S service) {
        super(new BorderLayout(5, 5));
        this.parentFrame = parentFrame;
        this.service = service;
        this.entityTypeName = entityTypeName;

        // Title label
        JLabel titleLabel = new JLabel(entityTypeName, SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Entity list with scrolling
        JScrollPane scrollPane = new JScrollPane(entityList);
        add(scrollPane, BorderLayout.CENTER);

        // Add New button at the bottom
        JButton addButton = new JButton("Add New " + entityTypeName);
        addButton.addActionListener(e -> showAddForm());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Mouse listener allows the same row to be clicked multiple times
        entityList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = entityList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        T selectedEntity = entityList.getModel().getElementAt(index);
                        if (selectedEntity != null) {
                            showDetails(selectedEntity);
                        }
                    }
                }
            }
        });

        // Refresh entities at initialisation
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

    protected void showDetails(T entity) {
        // Get the full entity with details
        T fullEntity = service.getByIdWithDetails(getEntityId(entity));
        // Create and show the details dialogue
        AbstractDetails<T> detailsDialog = createDetailsDialog(fullEntity, this::refreshEntities);
        detailsDialog.showDetailsDialog();
    }

    protected void showAddForm() {
        JDialog dialog = new JDialog(parentFrame, "Add New " + entityTypeName, true);
        dialog.setContentPane(createForm());
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
        refreshEntities();
    }

    protected boolean matchesNameField(String name, String query) {
        return name != null && name.toLowerCase().contains(query.toLowerCase());
    }

    // Abstract methods that must be implemented
    protected abstract boolean matchesQuery(T entity, String query);

    protected abstract Comparator<T> getComparator();

    protected abstract Long getEntityId(T entity);

    protected abstract JComponent createForm();

    protected abstract AbstractDetails<T> createDetailsDialog(T entity, Runnable refreshCallback);
}
