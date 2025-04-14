package com.tuka.comiccharacters.ui.panel;

import com.tuka.comiccharacters.model.Series;
import com.tuka.comiccharacters.service.PublisherService;
import com.tuka.comiccharacters.service.SeriesService;

import javax.swing.*;
import java.util.List;

import static com.tuka.comiccharacters.ui.MainApp.showError;
import static com.tuka.comiccharacters.ui.MainApp.showSuccess;

public class PublisherPanel extends JPanel {

    public final SeriesService seriesService = new SeriesService();
    public final JList<Series> seriesList;

    public PublisherPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Publisher"));

        JTextField nameField = new JTextField(15);
        PublisherService publisherService = new PublisherService();

        seriesList = new JList<>();
        seriesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        refreshSeries();
        JScrollPane scrollPane = new JScrollPane(seriesList);

        add(new JLabel("Publisher Name:"));
        add(nameField);
        add(Box.createVerticalStrut(5));
        add(new JLabel("Select Series:"));
        add(scrollPane);
        add(Box.createVerticalStrut(10));

        JButton addButton = new JButton("Add Publisher");
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showError("Publisher name is required.");
                return;
            }
            List<Series> selectedSeries = seriesList.getSelectedValuesList();
            publisherService.addPublisher(name, selectedSeries);
            showSuccess("Publisher added!");
            nameField.setText("");
            seriesList.clearSelection();
        });

        add(addButton);
    }

    public void refreshSeries() {
        List<Series> updatedSeries = seriesService.getAllSeries();
        seriesList.setListData(updatedSeries.toArray(new Series[0]));
    }
}
