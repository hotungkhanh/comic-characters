package com.tuka.comiccharacters.ui;

import com.tuka.comiccharacters.service.SeriesService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Comic Book Tracker");
        frame.setLayout(new FlowLayout()); // Use FlowLayout for better arrangement

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField(20);

        JLabel startYearLabel = new JLabel("Start Year:");
        JTextField startYearField = new JTextField(20);

        JButton addButton = new JButton("Add Comic Book");

        SeriesService service = new SeriesService();
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String startYearText = startYearField.getText();

                if (title.trim().isEmpty() || startYearText.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter both title and start year.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int startYear = Integer.parseInt(startYearText);
                    service.addComicBook(title, startYear); // Call the service with the correct types
                    JOptionPane.showMessageDialog(frame, "Comic Book added!");
                    titleField.setText("");
                    startYearField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Start Year must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    startYearField.setText("");
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(titleLabel);
        panel.add(titleField);
        panel.add(startYearLabel);
        panel.add(startYearField);
        panel.add(addButton);

        frame.setContentPane(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}