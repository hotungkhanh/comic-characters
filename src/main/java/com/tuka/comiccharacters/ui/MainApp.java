package com.tuka.comiccharacters.ui;

import com.tuka.comiccharacters.service.SeriesService;
import com.tuka.comiccharacters.service.CharacterService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Comic Book Tracker");
        frame.setLayout(new GridLayout(2, 1)); // 2 rows: Comic Book panel + Character panel

        // ================= Comic Book Form ===================
        JPanel comicPanel = new JPanel(new FlowLayout());

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField(15);

        JLabel startYearLabel = new JLabel("Start Year:");
        JTextField startYearField = new JTextField(5);

        JButton addComicButton = new JButton("Add Comic Book");
        SeriesService seriesService = new SeriesService();

        addComicButton.addActionListener(new ActionListener() {
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
                    seriesService.addComicBook(title, startYear);
                    JOptionPane.showMessageDialog(frame, "Comic Book added!");
                    titleField.setText("");
                    startYearField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Start Year must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    startYearField.setText("");
                }
            }
        });

        comicPanel.add(new JLabel("Comic Book"));
        comicPanel.add(titleLabel);
        comicPanel.add(titleField);
        comicPanel.add(startYearLabel);
        comicPanel.add(startYearField);
        comicPanel.add(addComicButton);

        // ================= Character Form ===================
        JPanel characterPanel = new JPanel(new FlowLayout());

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(10);

        JLabel aliasLabel = new JLabel("Alias:");
        JTextField aliasField = new JTextField(10);

        JLabel publisherLabel = new JLabel("Publisher:");
        JTextField publisherField = new JTextField(10);

        JButton addCharacterButton = new JButton("Add Character");
        CharacterService characterService = new CharacterService();

        addCharacterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String alias = aliasField.getText();
                String publisher = publisherField.getText();

                if (name.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Character name is required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                characterService.addCharacter(name, alias, publisher);
                JOptionPane.showMessageDialog(frame, "Character added!");
                nameField.setText("");
                aliasField.setText("");
                publisherField.setText("");
            }
        });

        characterPanel.add(new JLabel("Character"));
        characterPanel.add(nameLabel);
        characterPanel.add(nameField);
        characterPanel.add(aliasLabel);
        characterPanel.add(aliasField);
        characterPanel.add(publisherLabel);
        characterPanel.add(publisherField);
        characterPanel.add(addCharacterButton);

        // Add both panels to frame
        frame.add(comicPanel);
        frame.add(characterPanel);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
