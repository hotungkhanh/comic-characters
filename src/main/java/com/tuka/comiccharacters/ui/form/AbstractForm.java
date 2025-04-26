package com.tuka.comiccharacters.ui.form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public abstract class AbstractForm extends JPanel {

    protected static final long DEFAULT_SEARCH_DELAY = 300; // milliseconds
    protected final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
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
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
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
    protected <T> void addItemRemovalListener(JList<T> list, DefaultListModel<T> model, String menuItemText, Runnable removalAction) {
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
     * Sets up a delayed search handler for text search fields
     *
     * @param searchTask     Reference to the current search task that can be cancelled
     * @param searchField    The text field to monitor for changes
     * @param searchFunction The function to execute when search is triggered
     * @param delay          The delay in milliseconds before executing the search
     * @return ScheduledFuture representing the scheduled search task
     */
    protected ScheduledFuture<?> setupDelayedSearch(ScheduledFuture<?> searchTask, JTextField searchField, Runnable searchFunction, long delay) {
        // Cancel previous search if it exists
        if (searchTask != null) {
            searchTask.cancel(true);
        }

        // Schedule new search with delay
        return scheduler.schedule(searchFunction, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Creates a reusable search panel with a label and search field
     *
     * @param label       The label text for the search field
     * @param searchField The search field component
     * @return The configured search panel
     */
    protected JPanel createSearchPanel(String label, JTextField searchField) {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(new JLabel(label), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        return searchPanel;
    }

    /**
     * Filters a list model based on search text and a filter predicate
     *
     * @param <T>             The type of elements in the model
     * @param searchText      The text to search for
     * @param sourceItems     The collection of all items to search within
     * @param targetModel     The model to populate with filtered results
     * @param filterPredicate The predicate to determine if an item matches
     */
    protected <T> void performFilteredSearch(String searchText, Iterable<T> sourceItems, DefaultListModel<T> targetModel, Predicate<T> filterPredicate) {
        // Clear the target model
        targetModel.clear();

        // If search is empty or placeholder, return
        if (searchText == null || searchText.isEmpty()) {
            return;
        }

        // Add matching items to the model
        for (T item : sourceItems) {
            if (filterPredicate.test(item)) {
                targetModel.addElement(item);
            }
        }
    }

    /**
     * Adds selected items from one list to another
     *
     * @param <T>         The type of elements in the lists
     * @param sourceList  The list containing items to add
     * @param targetModel The model to add items to
     * @param postAction  Optional action to perform after items are added
     */
    protected <T> void addSelectedItemsToModel(JList<T> sourceList, DefaultListModel<T> targetModel, Runnable postAction) {
        List<T> selectedItems = sourceList.getSelectedValuesList();
        for (T item : selectedItems) {
            if (!listModelContains(targetModel, item)) {
                targetModel.addElement(item);
            }
        }

        // Clear selection after adding
        sourceList.clearSelection();

        // Run post-action if provided
        if (postAction != null) {
            postAction.run();
        }
    }

    /**
     * Checks if a list model contains a specific item
     *
     * @param <T>   The type of elements in the model
     * @param model The model to check
     * @param item  The item to look for
     * @return true if the model contains the item
     */
    protected <T> boolean listModelContains(DefaultListModel<T> model, T item) {
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).equals(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses a string to a LocalDate, with helpful error message
     *
     * @param dateString The string to parse
     * @param fieldName  The name of the field for error reporting
     * @return The parsed LocalDate or null if parsing failed
     */
    protected LocalDate parseLocalDate(String dateString, String fieldName) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            showError("Invalid date format in " + fieldName + ". Please use YYYY-MM-DD.");
            return null;
        }
    }

    /**
     * Creates a typical search and results panel with add button
     *
     * @param <T>           The type of elements in the lists
     * @param title         The title for the panel
     * @param searchField   The search field component
     * @param resultsList   The list to display search results
     * @param addButtonText The text for the add button
     * @param addAction     The action to perform when add button is clicked
     * @return The configured panel
     */
    protected <T> JPanel createSearchAndResultsPanel(String title, JTextField searchField, JList<T> resultsList, String addButtonText, ActionListener addAction) {
        JPanel panel = createTitledPanel(title);
        panel.setLayout(new BorderLayout());

        // Create search panel
        JPanel searchPanel = createSearchPanel("Search:", searchField);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Create results panel
        JScrollPane resultsScrollPane = new JScrollPane(resultsList);
        resultsScrollPane.setPreferredSize(new Dimension(300, 80));
        panel.add(resultsScrollPane, BorderLayout.CENTER);

        // Create add button
        JButton addButton = new JButton(addButtonText);
        addButton.addActionListener(addAction);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
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
