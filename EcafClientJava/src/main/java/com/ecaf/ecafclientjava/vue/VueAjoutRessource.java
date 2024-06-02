package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.technique.HttpResponseWrapper;
import com.ecaf.ecafclientjava.technique.HttpService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;




public class VueAjoutRessource extends Pane {

    private TextField nomField = new TextField();
    private ComboBox<String> typeComboBox = new ComboBox<>();
    private TextField quantiteField = new TextField();
    private TextField emplacementField = new TextField();
    private Button submitButton = new Button("Submit");
    private Button resetButton = new Button("Reset");

    public VueAjoutRessource() {
        // GridPane for form
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Form elements
        Label nomLabel = new Label("Nom:");
        GridPane.setConstraints(nomLabel, 0, 0);
        GridPane.setConstraints(nomField, 1, 0);

        Label typeLabel = new Label("Type:");
        GridPane.setConstraints(typeLabel, 0, 1);
        typeComboBox.getItems().addAll("Vetement", "Argent", "Alimentaire", "Jouet", "Matériel maison divers", "Materiel", "Autre");
        GridPane.setConstraints(typeComboBox, 1, 1);

        Label quantiteLabel = new Label("Quantité:");
        GridPane.setConstraints(quantiteLabel, 0, 2);
        GridPane.setConstraints(quantiteField, 1, 2);

        Label emplacementLabel = new Label("Emplacement:");
        GridPane.setConstraints(emplacementLabel, 0, 3);
        GridPane.setConstraints(emplacementField, 1, 3);

        GridPane.setConstraints(submitButton, 1, 4);
        GridPane.setConstraints(resetButton, 2, 4);

        submitButton.setOnAction(e -> handleSubmit());
        resetButton.setOnAction(e -> handleReset());

        grid.getChildren().addAll(nomLabel, nomField, typeLabel, typeComboBox, quantiteLabel, quantiteField, emplacementLabel, emplacementField, submitButton, resetButton);

        // Adding border to the grid
        grid.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT)));

        // Main layout with HBox
        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(20, 20, 20, 20));
        mainLayout.getChildren().addAll(grid);

        getChildren().add(mainLayout);
    }

    private void handleSubmit() {
        String nom = nomField.getText();
        String type = typeComboBox.getValue();
        Integer quantite = null;
        try {
            quantite = Integer.parseInt(quantiteField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Quantité must be a number.");
            return;
        }
        String emplacement = emplacementField.getText();

        // Add validation and error handling
        if (nom.isEmpty() || type == null || quantite == null || emplacement.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill all required fields.");
            return;
        }

        // Prepare JSON or any other format to send to backend
        String requestBody = String.format("{\"nom\":\"%s\", \"type\":\"%s\", \"quantite\":%d, \"emplacement\":\"%s\"}", nom, type, quantite, emplacement);

        // Send request to backend
        try {
            HttpService httpService = new HttpService();
            HttpResponseWrapper responseWrapper = httpService.sendPostRequest("ressources", requestBody);
            if (responseWrapper.getStatusCode() == 201) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Resource added successfully!");
                handleReset();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add resource.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred.");
        }
    }

    private void handleReset() {
        nomField.clear();
        typeComboBox.setValue(null);
        quantiteField.clear();
        emplacementField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
