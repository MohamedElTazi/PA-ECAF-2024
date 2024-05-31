package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.technique.HttpResponseWrapper;
import com.ecaf.ecafclientjava.technique.HttpService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VueFormTache extends Pane {

    private TextField descriptionField = new TextField();
    private DatePicker dateDebutPicker = new DatePicker();
    private DatePicker dateFinPicker = new DatePicker();
    private ComboBox<String> statutComboBox = new ComboBox<>();
    private TextField responsableIdField = new TextField();
    private Button submitButton = new Button("Submit");
    private Button resetButton = new Button("Reset");

    public VueFormTache() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Description
        Label descriptionLabel = new Label("Description:");
        GridPane.setConstraints(descriptionLabel, 0, 0);
        GridPane.setConstraints(descriptionField, 1, 0);

        // Date Debut
        Label dateDebutLabel = new Label("Start Date:");
        GridPane.setConstraints(dateDebutLabel, 0, 1);
        GridPane.setConstraints(dateDebutPicker, 1, 1);

        // Date Fin
        Label dateFinLabel = new Label("End Date:");
        GridPane.setConstraints(dateFinLabel, 0, 2);
        GridPane.setConstraints(dateFinPicker, 1, 2);

        // Statut
        Label statutLabel = new Label("Status:");
        statutComboBox.getItems().addAll("En cours", "Fini");
        GridPane.setConstraints(statutLabel, 0, 3);
        GridPane.setConstraints(statutComboBox, 1, 3);

        // Responsable ID
        Label responsableIdLabel = new Label("Responsible ID:");
        GridPane.setConstraints(responsableIdLabel, 0, 4);
        GridPane.setConstraints(responsableIdField, 1, 4);

        // Buttons
        GridPane.setConstraints(submitButton, 1, 5);
        GridPane.setConstraints(resetButton, 2, 5);

        submitButton.setOnAction(e -> handleSubmit());
        resetButton.setOnAction(e -> handleReset());

        grid.getChildren().addAll(descriptionLabel, descriptionField, dateDebutLabel, dateDebutPicker,
                dateFinLabel, dateFinPicker, statutLabel, statutComboBox, responsableIdLabel, responsableIdField,
                submitButton, resetButton);

        getChildren().add(grid);
    }

    private void handleSubmit() {
        String description = descriptionField.getText();
        LocalDateTime dateDebut = dateDebutPicker.getValue().atStartOfDay();
        LocalDateTime dateFin = dateFinPicker.getValue() != null ? dateFinPicker.getValue().atStartOfDay() : null;
        String statut = statutComboBox.getValue();
        Integer responsableId = null;
        try {
            responsableId = Integer.parseInt(responsableIdField.getText());
        } catch (NumberFormatException e) {
            // Handle exception if responsibleId is not a number
        }

        // Add validation and error handling
        if (description.isEmpty() || dateDebut == null || statut == null) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill all required fields.");
            return;
        }

        // Format dates for database
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateDebutStr = dateDebut.format(formatter);
        String dateFinStr = dateFin != null ? dateFin.format(formatter) : null;

        // Prepare JSON or any other format to send to backend
        String requestBody = String.format("{\"description\":\"%s\", \"dateDebut\":\"%s\", \"dateFin\":\"%s\", \"statut\":\"%s\", \"responsable\":%d}",
                description, dateDebutStr, dateFinStr, statut, responsableId);

        // Send request to backend
        try {
            HttpService httpService = new HttpService();
            HttpResponseWrapper responseWrapper = httpService.sendPostRequest("taches",requestBody);
            if (responseWrapper.getStatusCode() == 201) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Task added successfully!");
                handleReset();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add task.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred.");
        }
    }

    private void handleReset() {
        descriptionField.clear();
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        statutComboBox.setValue(null);
        responsableIdField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

