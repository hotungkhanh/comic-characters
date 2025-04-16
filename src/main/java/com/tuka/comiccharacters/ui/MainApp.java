package com.tuka.comiccharacters.ui;

import com.tuka.comiccharacters.ui.display.CreatorDisplay;
import com.tuka.comiccharacters.ui.panel.*;

import javax.swing.*;
import java.awt.*;

public class MainApp {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tuka's Comic Book Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridLayout(1, 1, 10, 10));

        // Display panels
        CreatorDisplay creatorDisplay = new CreatorDisplay();
        mainPanel.add(creatorDisplay);

        JButton addCreatorButton = new JButton("Add Creator");
        addCreatorButton.addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Add Creator", true);
            dialog.setContentPane(new CreatorPanel());
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);

            // Optionally refresh UI after closing dialog
            creatorDisplay.refreshCreators();
        });


        frame.add(mainPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
