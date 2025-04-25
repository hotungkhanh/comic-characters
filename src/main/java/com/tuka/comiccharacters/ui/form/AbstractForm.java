package com.tuka.comiccharacters.ui.form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractForm extends JPanel {

    protected JPanel formPanel;
    protected JButton submitButton;
    protected List<JComponent> requiredFields;
    protected boolean isEditMode;

    /**
     * Creates a new form with the specified submit button text
     *
     * @param submitButtonText The text to display on the submit button
     */
    public AbstractForm(String submitButtonText) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel = new JPanel(new GridBagLayout());
        submitButton = new JButton(submitButtonText);
        requiredFields = new ArrayList<>();
        isEditMode = false;

        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);
    }

    /**
     * Adds a component to the form with the specified label
     *
     * @param label      The label for the component
     * @param component  The component to add
     * @param row        The row to add the component to
     * @param isRequired Whether the field is required
     * @return The next row number
     */
    protected int addFormField(String label, JComponent component, int row, boolean isRequired) {
        GridBagConstraints gbc = createDefaultConstraints();

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        formPanel.add(component, gbc);

        if (isRequired) {
            requiredFields.add(component);
        }

        return row + 1;
    }

    /**
     * Adds a text field to the form with the specified label
     *
     * @param label      The label for the text field
     * @param textField  The text field to add
     * @param row        The row to add the text field to
     * @param isRequired Whether the field is required
     * @return The next row number
     */
    protected int addTextField(String label, JTextField textField, int row, boolean isRequired) {
        return addFormField(label, textField, row, isRequired);
    }

    /**
     * Adds a text area to the form with the specified label
     *
     * @param label      The label for the text area
     * @param textArea   The text area to add
     * @param row        The row to add the text area to
     * @param height     The height of the text area in rows
     * @param isRequired Whether the field is required
     * @return The next row number
     */
    protected int addTextArea(String label, JTextArea textArea, int row, int height, boolean isRequired) {
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, height * 20));

        return addFormField(label, scrollPane, row, isRequired);
    }

    /**
     * Adds a dropdown to the form with the specified label
     *
     * @param label      The label for the dropdown
     * @param dropdown   The dropdown to add
     * @param row        The row to add the dropdown to
     * @param isRequired Whether the field is required
     * @return The next row number
     */
    protected int addDropdown(String label, JComboBox<?> dropdown, int row, boolean isRequired) {
        return addFormField(label, dropdown, row, isRequired);
    }

    /**
     * Adds a checkbox to the form
     *
     * @param checkbox The checkbox to add
     * @param row      The row to add the checkbox to
     * @return The next row number
     */
    protected int addCheckbox(JCheckBox checkbox, int row) {
        GridBagConstraints gbc = createDefaultConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(checkbox, gbc);
        return row + 1;
    }

    /**
     * Adds a panel to the form that spans both columns
     *
     * @param panel The panel to add
     * @param row   The row to add the panel to
     * @return The next row number
     */
    protected int addPanel(JPanel panel, int row) {
        GridBagConstraints gbc = createDefaultConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        formPanel.add(panel, gbc);
        return row + 1;
    }

    /**
     * Creates a titled panel with a border
     *
     * @param title The title of the panel
     * @return The created panel
     */
    protected JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    /**
     * Creates a search field with placeholder text
     *
     * @param placeholderText The placeholder text to display
     * @return The created search field
     */
    protected JTextField createSearchField(String placeholderText) {
        JTextField searchField = new JTextField(15);
        searchField.setText(placeholderText);
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(placeholderText)) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText(placeholderText);
                }
            }
        });

        return searchField;
    }

    /**
     * Creates a dropdown with a null item renderer
     *
     * @param items    The items to add to the dropdown
     * @param nullText The text to display for null items
     * @return The created dropdown
     */
    protected <T> JComboBox<T> createNullableDropdown(T[] items, String nullText) {
        JComboBox<T> dropdown = new JComboBox<>(items);
        dropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? nullText : value.toString());
                return this;
            }
        });
        return dropdown;
    }

    /**
     * Creates a default set of GridBagConstraints
     *
     * @return The created constraints
     */
    protected GridBagConstraints createDefaultConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        return gbc;
    }

    /**
     * Adds a listener to the submit button
     *
     * @param listener The listener to add
     */
    public void addSubmitListener(ActionListener listener) {
        submitButton.addActionListener(listener);
    }

    /**
     * Sets the text of the submit button
     *
     * @param text The text to set
     */
    public void setSubmitButtonText(String text) {
        submitButton.setText(text);
    }

    /**
     * Removes all listeners from the submit button
     */
    public void removeAllSubmitListeners() {
        for (ActionListener al : submitButton.getActionListeners()) {
            submitButton.removeActionListener(al);
        }
    }

    /**
     * Adds a context menu for removing items from a JList
     *
     * @param list          The JList to add the removal listener to
     * @param model         The list model containing the items
     * @param menuItemText  The text for the removal menu item
     * @param removalAction Optional additional action to perform after removal
     */
    protected <T> void addItemRemovalListener(JList<T> list, DefaultListModel<T> model,
                                              String menuItemText, Runnable removalAction) {
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = list.locationToIndex(e.getPoint());
                    if (index >= 0 && index < model.getSize()) {
                        list.setSelectedIndex(index);
                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem removeItem = getJMenuItem();
                        popupMenu.add(removeItem);
                        popupMenu.show(list, e.getX(), e.getY());
                    }
                }
            }

            private JMenuItem getJMenuItem() {
                JMenuItem removeItem = new JMenuItem(menuItemText);
                removeItem.addActionListener(event -> {
                    int selectedIndex = list.getSelectedIndex();
                    if (selectedIndex != -1) {
                        model.remove(selectedIndex);
                        if (removalAction != null) {
                            removalAction.run();
                        }
                    }
                });
                return removeItem;
            }
        });
    }

    /**
     * Shows an error message
     *
     * @param message The message to show
     */
    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a success message
     *
     * @param message The message to show
     */
    protected void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Sets the form to edit mode
     *
     * @param isEditMode Whether the form is in edit mode
     */
    protected void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        setSubmitButtonText(isEditMode ? "Save Changes" : "Add");
    }

    /**
     * Simple validation for URLs - could be enhanced for better validation
     *
     * @param url URL to validate
     * @return true if the URL seems valid
     */
    protected boolean isValidUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    /**
     * Resets the form fields
     */
    protected abstract void resetForm();

    /**
     * Builds the UI components of the form
     */
    protected abstract void buildUI();
}
