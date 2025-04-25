package com.tuka.comiccharacters.ui.details;

import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.ui.MainApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractDetails<T> {

    protected final Component parent;
    protected final T entity;
    protected final Runnable refreshCallback;
    protected final GridBagConstraints gbc;
    protected JDialog currentDialog;

    public AbstractDetails(Component parent, T entity, Runnable refreshCallback) {
        this.parent = parent;
        this.entity = entity;
        this.refreshCallback = refreshCallback;
        this.gbc = defaultGbc();
    }

    private static <T> JList<T> createSelectableList(Function<T, String> nameExtractor, DefaultListModel<T> listModel) {
        JList<T> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer((list1, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(nameExtractor.apply(value));
            if (isSelected) {
                label.setBackground(list1.getSelectionBackground());
                label.setForeground(list1.getSelectionForeground());
                label.setOpaque(true);
            }
            return label;
        });
        return list;
    }

    public void showDetailsDialog() {
        // Default size - child classes should override this method to specify their own dimensions
        showDetailsDialog(600, 500);
    }

    public void showDetailsDialog(int windowWidth, int windowHeight) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), getTitle(), true);
        this.currentDialog = dialog;
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(windowWidth, windowHeight);
        dialog.setLocationRelativeTo(parent);

        dialog.add(getMainPanel(dialog), BorderLayout.CENTER);
        dialog.add(getButtonPanel(dialog), BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    protected JPanel getButtonPanel(JDialog dialog) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        editButton.addActionListener(_ -> {
            dialog.dispose();
            showEditDialog();
        });

        deleteButton.addActionListener(_ -> {
            int confirm = JOptionPane.showConfirmDialog(parent, getDeleteConfirmationMessage(), "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteEntity();
                dialog.dispose();
                refreshCallback.run();
            }
        });

        panel.add(editButton);
        panel.add(deleteButton);
        return panel;
    }

    protected abstract JPanel getMainPanel(JDialog dialog);

    protected abstract String getTitle();

    protected abstract void showEditDialog();

    protected abstract void deleteEntity();

    // ===== Common UI Component Methods =====

    protected abstract String getDeleteConfirmationMessage();

    protected int addLabelValue(JPanel panel, String label, String value, int row) {
        if (value == null || value.isBlank()) return row;

        panel.add(createLabel(label, row, 0, false), labelConstraints(row));
        panel.add(new JLabel(value), valueConstraints(row, GridBagConstraints.HORIZONTAL, 1.0));
        return row + 1;
    }

    protected int addTextArea(JPanel panel, String label, String text, int row, int rows) {
        if (text == null || text.isBlank()) return row;

        panel.add(createLabel(label, row, 0, false), labelConstraints(row));

        JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setBackground(panel.getBackground());
        textArea.setBorder(null);
        textArea.setFont(UIManager.getFont("Label.font"));
        textArea.setRows(rows);

        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, valueConstraints(row, GridBagConstraints.BOTH, 0.1));
        gbc.weighty = 0; // Reset
        return row + 1;
    }

    protected JPanel createMainInfoPanel() {
        return new JPanel(new GridBagLayout());
    }

    // ===== Navigable Lists =====

    /**
     * Adds a clickable publisher row that navigates to publisher details when clicked
     *
     * @param panel            The panel to add the clickable publisher to
     * @param row              The current row in the grid layout
     * @param publisher        The publisher entity to display and navigate to
     * @param publisherService The service used to fetch the full publisher details
     * @return The next available row number
     */
    protected int addClickablePublisher(JPanel panel, int row, Publisher publisher, PublisherService publisherService) {
        if (publisher == null) {
            return row;
        }

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Publisher fetchedPublisher = publisherService.getByIdWithDetails(publisher.getId());
                if (fetchedPublisher != null) {
                    currentDialog.dispose();
                    new PublisherDetails(parent, fetchedPublisher, refreshCallback).showDetailsDialog();
                } else {
                    MainApp.showError("Could not load publisher details.");
                }
            }
        };

        return addClickableLabel(panel, "Publisher:", publisher.getName(), row, mouseAdapter, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    protected int addClickableLabel(JPanel panel, String labelText, String valueText, int row, MouseAdapter mouseAdapter, Cursor cursor) {
        if (valueText == null || valueText.isBlank()) return row;

        panel.add(createLabel(labelText, row, 0, false), labelConstraints(row));

        JLabel valueLabel = new JLabel(valueText);
        if (mouseAdapter != null) {
            valueLabel.setForeground(Color.BLUE);
            valueLabel.setCursor(cursor);
            valueLabel.addMouseListener(mouseAdapter);
        }

        panel.add(valueLabel, valueConstraints(row, GridBagConstraints.HORIZONTAL, 1.0));
        return row + 1;
    }

    /**
     * Creates a clickable list panel with items that can be navigated to on double-click
     */
    protected <E> JPanel createClickableListPanel(String title, List<E> items, Function<E, String> nameExtractor, Consumer<E> onDoubleClick) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        DefaultListModel<E> listModel = new DefaultListModel<>();
        items.forEach(listModel::addElement);

        JList<E> list = createSelectableList(nameExtractor, listModel);

        list.addMouseListener(getListDoubleClickListener(items, onDoubleClick));

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(200, 120));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Adds a clickable list panel to the parent panel and updates the grid position
     */
    protected <E> int addNavigableListPanel(JPanel parentPanel, String title, List<E> items, Function<E, String> nameExtractor, Consumer<E> onDoubleClick, int row) {
        if (items == null || items.isEmpty()) return row;

        JPanel panel = createClickableListPanel(title, items, nameExtractor, onDoubleClick);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        parentPanel.add(panel, gbc);
        return row;
    }

    /**
     * Creates a mouse adapter for handling double-click navigation on lists
     */
    protected <E> MouseAdapter getListDoubleClickListener(List<E> items, Consumer<E> onDoubleClick) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JList<?> list = (JList<?>) e.getSource();
                    int selectedIndex = list.getSelectedIndex();
                    if (selectedIndex >= 0 && selectedIndex < items.size()) {
                        onDoubleClick.accept(items.get(selectedIndex));
                    }
                }
            }
        };
    }

    // ===== Constraint and Layout Helper Methods =====

    private GridBagConstraints defaultGbc() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        return c;
    }

    private GridBagConstraints labelConstraints(int row) {
        GridBagConstraints c = (GridBagConstraints) gbc.clone();
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        return c;
    }

    private GridBagConstraints valueConstraints(int row, int fill, double weightY) {
        GridBagConstraints c = (GridBagConstraints) gbc.clone();
        c.gridx = 1;
        c.gridy = row;
        c.fill = fill;
        c.weighty = weightY;
        return c;
    }

    private JLabel createLabel(String text, int row, int col, boolean bold) {
        JLabel label = new JLabel(text);
        if (bold) {
            label.setFont(label.getFont().deriveFont(Font.BOLD));
        }
        return label;
    }
}
