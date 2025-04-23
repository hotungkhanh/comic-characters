package com.tuka.comiccharacters.ui.details;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractDetails<T> {

    protected final Component parent;
    protected final T entity;
    protected final Runnable refreshCallback;
    protected final GridBagConstraints gbc;

    public AbstractDetails(Component parent, T entity, Runnable refreshCallback) {
        this.parent = parent;
        this.entity = entity;
        this.refreshCallback = refreshCallback;
        this.gbc = defaultGbc();
    }

    public void showDetailsDialog(int windowWidth, int windowHeight) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), getTitle(), true);
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

    protected <E> JPanel createListPanel(String title, List<E> items, Function<E, String> toStringMapper, int preferredHeight) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (E item : items) {
            listModel.addElement(toStringMapper.apply(item));
        }

        JList<String> jList = new JList<>(listModel);
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(jList);
        scrollPane.setPreferredSize(new Dimension(400, preferredHeight));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    protected JPanel createMainInfoPanel() {
        return new JPanel(new GridBagLayout());
    }

    protected int addClickableLabel(JPanel panel, String labelText, String valueText, int row, MouseAdapter mouseAdapter, Cursor cursor) {
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

    // === Helper Methods ===

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
