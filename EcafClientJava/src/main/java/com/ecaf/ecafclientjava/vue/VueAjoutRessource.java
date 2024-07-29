package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.entites.Ressource;
import com.ecaf.ecafclientjava.technique.HttpResponseWrapper;
import com.ecaf.ecafclientjava.technique.HttpService;
import com.ecaf.ecafclientjava.technique.sqllite.NetworkUtil;
import com.ecaf.ecafclientjava.technique.Theme;
import com.ecaf.ecafclientjava.technique.sqllite.SQLiteHelper;
import com.ecaf.ecafclientjava.technique.sqllite.SyncStatus;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class VueAjoutRessource extends BorderPane {

    private TextField nomField = new TextField();
    private ComboBox<String> typeComboBox = new ComboBox<>();
    private TextField quantiteField = new TextField();
    private TextField emplacementField = new TextField();
    private Button submitButton = new Button("Submit");
    private Button resetButton = new Button("Reset");

    public VueAjoutRessource() {
        // GridPane for form
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(40, 40, 40, 40));
        grid.setVgap(30);
        grid.setHgap(30);
        grid.getStyleClass().add("grid-pane");

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
        submitButton.getStyleClass().add("button-submit");
        resetButton.getStyleClass().add("button-reset");
        grid.getChildren().addAll(nomLabel, nomField, typeLabel, typeComboBox, quantiteLabel, quantiteField, emplacementLabel, emplacementField, submitButton, resetButton);

        // Adding border to the grid
        grid.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT)));

        // Main layout with HBox
        HBox mainLayout = new HBox(50);
        mainLayout.setPadding(new Insets(40, 40, 40, 40));
        mainLayout.getStyleClass().add("hbox");
        mainLayout.getChildren().addAll(grid);

        setCenter(mainLayout);

        // Apply initial CSS
        applyCurrentTheme();
    }

    private void handleSubmit() {
        String nom = nomField.getText();
        String type = typeComboBox.getValue();
        Integer quantite = null;
        try {
            quantite = Integer.parseInt(quantiteField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Formulaire!", "Quantité doit être un chiffre.");
            return;
        }
        String emplacement = emplacementField.getText();

        // Add validation and error handling
        if (nom.isEmpty() || type == null || quantite == null || emplacement.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur Formulaire!", "Veuillez remplir tous les champs du formulaire.");
            return;
        }

        Ressource nouvelleRessource = new Ressource(0, nom, type, quantite, emplacement, SyncStatus.NEW.getStatus());

        // Check network status
        if (NetworkUtil.isOnline()) {
            // Prepare JSON or any other format to send to backend
            String requestBody = String.format("{\"nom\":\"%s\", \"type\":\"%s\", \"quantite\":%d, \"sync_status\":\"%s\", \"emplacement\":\"%s\"}", nom, type, quantite, SyncStatus.SYNCED.getStatus(),emplacement);

            // Send request to backend
            try {
                HttpService httpService = new HttpService();
                HttpResponseWrapper responseWrapper = httpService.sendPostRequest("ressources", requestBody);
                if (responseWrapper.getStatusCode() == 201) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Ressource créée avec succès !");
                    handleReset();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'ajout d'une ressource.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue !");
            }
        } else {
            // Save to local SQLite database
            SQLiteHelper.createRessource(nouvelleRessource);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Ressource créée localement avec succès !");
            handleReset();
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

    public void applyCurrentTheme() {
        this.getStylesheets().clear();
        this.getStylesheets().add(getClass().getResource(Theme.themeTableauFormulaire).toExternalForm());
        this.setStyle("-fx-background-color: " + Theme.backgroudColorMain + ";");
    }
}
