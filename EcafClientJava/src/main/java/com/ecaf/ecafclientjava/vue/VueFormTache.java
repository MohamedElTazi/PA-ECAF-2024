package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.entites.User;
import com.ecaf.ecafclientjava.technique.HttpResponseWrapper;
import com.ecaf.ecafclientjava.technique.HttpService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.scene.layout.*;

public class VueFormTache extends Pane {

    private TextField descriptionField = new TextField();
    private DatePicker dateDebutPicker = new DatePicker();
    private DatePicker dateFinPicker = new DatePicker();
    private ComboBox<String> statutComboBox = new ComboBox<>();
    private TextField responsableIdField = new TextField();
    private Button submitButton = new Button("Submit");
    private Button resetButton = new Button("Reset");

    private TableView<User> adminTableView = new TableView<>();
    private TableView<User> adherentTableView = new TableView<>();

    public VueFormTache() {
        // GridPane for form
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Form elements
        Label descriptionLabel = new Label("Description:");
        GridPane.setConstraints(descriptionLabel, 0, 0);
        GridPane.setConstraints(descriptionField, 1, 0);

        Label dateDebutLabel = new Label("Start Date:");
        GridPane.setConstraints(dateDebutLabel, 0, 1);
        GridPane.setConstraints(dateDebutPicker, 1, 1);

        Label dateFinLabel = new Label("End Date:");
        GridPane.setConstraints(dateFinLabel, 0, 2);
        GridPane.setConstraints(dateFinPicker, 1, 2);

        Label statutLabel = new Label("Status:");
        statutComboBox.getItems().addAll("En cours", "Fini");
        GridPane.setConstraints(statutLabel, 0, 3);
        GridPane.setConstraints(statutComboBox, 1, 3);

        Label responsableIdLabel = new Label("Responsible ID:");
        GridPane.setConstraints(responsableIdLabel, 0, 4);
        GridPane.setConstraints(responsableIdField, 1, 4);

        GridPane.setConstraints(submitButton, 1, 5);
        GridPane.setConstraints(resetButton, 2, 5);

        submitButton.setOnAction(e -> handleSubmit());
        resetButton.setOnAction(e -> handleReset());

        grid.getChildren().addAll(descriptionLabel, descriptionField, dateDebutLabel, dateDebutPicker,
                dateFinLabel, dateFinPicker, statutLabel, statutComboBox, responsableIdLabel, responsableIdField,
                submitButton, resetButton);

        // Adding border to the grid
        grid.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT)));

        // VBox for tables
        VBox tablesBox = new VBox(30);
        tablesBox.setPadding(new Insets(20, 20, 20, 20));

        Label adminLabel = new Label("Administrators:");
        configureUserTableView(adminTableView);
        Label adherentLabel = new Label("Adherents:");
        configureUserTableView(adherentTableView);

        tablesBox.getChildren().addAll(adminLabel, adminTableView, adherentLabel, adherentTableView);

        // Main layout with HBox
        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(20, 20, 20, 20));
        mainLayout.getChildren().addAll(grid, tablesBox);

        // Fetch and populate data
        fetchAndPopulateUserData();

        getChildren().add(mainLayout);
    }

    private void configureUserTableView(TableView<User> tableView) {
        TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> nomColumn = new TableColumn<>("Nom");
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<User, String> prenomColumn = new TableColumn<>("Prenom");
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        tableView.getColumns().addAll(idColumn, nomColumn, prenomColumn, emailColumn, roleColumn);
    }

    private void fetchAndPopulateUserData() {
        HttpService httpService = new HttpService();
        try {
            List<User> admins = httpService.getUsersByRole("Administrateur");
            List<User> adherents = httpService.getUsersByRole("Adherent");

            ObservableList<User> adminData = FXCollections.observableList(admins);
            ObservableList<User> adherentData = FXCollections.observableList(adherents);

            adminTableView.setItems(adminData);
            adherentTableView.setItems(adherentData);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
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
            HttpResponseWrapper responseWrapper = httpService.sendPostRequest("taches", requestBody);
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
