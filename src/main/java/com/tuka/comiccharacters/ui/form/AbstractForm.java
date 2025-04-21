package com.tuka.comiccharacters.ui.form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public abstract class AbstractForm extends JPanel {

    protected JPanel formPanel;
    protected JButton submitButton;

    public AbstractForm(String title, String submitButtonText) {
        setLayout(new BorderLayout());

        formPanel = new JPanel(new GridLayout(0, 1));
        formPanel.setBorder(BorderFactory.createTitledBorder(title));

        submitButton = new JButton(submitButtonText);

        add(formPanel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);
    }

    protected void addFormField(String label, JComponent component) {
        formPanel.add(new JLabel(label + ":"));
        formPanel.add(component);
    }

    public void addSubmitListener(ActionListener listener) {
        submitButton.addActionListener(listener);
    }

    public void setSubmitButtonText(String text) {
        submitButton.setText(text);
    }

    public void removeAllSubmitListeners() {
        for (ActionListener al : submitButton.getActionListeners()) {
            submitButton.removeActionListener(al);
        }
    }
}