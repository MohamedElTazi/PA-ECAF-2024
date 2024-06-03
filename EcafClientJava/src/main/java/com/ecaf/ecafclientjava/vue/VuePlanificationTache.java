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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javafx.scene.layout.*;

public class VuePlanificationTache extends Pane {

    private TextField descriptionField = new TextField();
    private DatePicker dateDebutPicker = new DatePicker();
    private DatePicker dateFinPicker = new DatePicker();
    private ComboBox<String> statutComboBox = new ComboBox<>();
    private ComboBox<Integer> responsableIdComboBox = new ComboBox<>();
    private Button submitButton = new Button("Submit");
    private Button resetButton = new Button("Reset");

    private TableView<User> userTableView = new TableView<>();

    public VuePlanificationTache() {
        // GridPane for form
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(40, 40, 40, 40));
        grid.setVgap(30);
        grid.setHgap(30);
        grid.getStyleClass().add("grid-pane");

        // Form elements
        Label descriptionLabel = new Label("Description:");
        descriptionLabel.getStyleClass().add("label");
        GridPane.setConstraints(descriptionLabel, 0, 0);
        GridPane.setConstraints(descriptionField, 1, 0);
        descriptionField.getStyleClass().add("text-field");

        Label dateDebutLabel = new Label("Start Date:");
        dateDebutLabel.getStyleClass().add("label");
        GridPane.setConstraints(dateDebutLabel, 0, 1);
        GridPane.setConstraints(dateDebutPicker, 1, 1);
        dateDebutPicker.getStyleClass().add("date-picker");

        Label dateFinLabel = new Label("End Date:");
        dateFinLabel.getStyleClass().add("label");
        GridPane.setConstraints(dateFinLabel, 0, 2);
        GridPane.setConstraints(dateFinPicker, 1, 2);
        dateFinPicker.getStyleClass().add("date-picker");

        Label statutLabel = new Label("Status:");
        statutLabel.getStyleClass().add("label");
        statutComboBox.getItems().addAll("En cours", "Fini");
        GridPane.setConstraints(statutLabel, 0, 3);
        GridPane.setConstraints(statutComboBox, 1, 3);
        statutComboBox.getStyleClass().add("combo-box");

        Label responsableIdLabel = new Label("Responsible ID:");
        responsableIdLabel.getStyleClass().add("label");
        GridPane.setConstraints(responsableIdLabel, 0, 4);
        GridPane.setConstraints(responsableIdComboBox, 1, 4);
        responsableIdComboBox.getStyleClass().add("combo-box");

        GridPane.setConstraints(submitButton, 1, 5);
        submitButton.getStyleClass().add("button");
        GridPane.setConstraints(resetButton, 2, 5);
        resetButton.getStyleClass().add("button");

        submitButton.setOnAction(e -> handleSubmit());
        resetButton.setOnAction(e -> handleReset());

        grid.getChildren().addAll(descriptionLabel, descriptionField, dateDebutLabel, dateDebutPicker,
                dateFinLabel, dateFinPicker, statutLabel, statutComboBox, responsableIdLabel, responsableIdComboBox,
                submitButton, resetButton);

        // Adding border to the grid
        grid.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT)));

        // VBox for table
        VBox tableBox = new VBox(100);
        tableBox.setPadding(new Insets(70, 70, 70, 70));
        tableBox.getStyleClass().add("vbox");

        Label userLabel = new Label("Users:");
        userLabel.getStyleClass().add("label");
        configureUserTableView(userTableView);
        tableBox.getChildren().addAll(userLabel, userTableView);

        // Main layout with HBox
        HBox mainLayout = new HBox(50);
        mainLayout.setPadding(new Insets(40, 40, 40, 40));
        mainLayout.getStyleClass().add("hbox");
        mainLayout.getChildren().addAll(grid, tableBox);

        // Fetch and populate data
        fetchAndPopulateUserData();

        getChildren().add(mainLayout);

        // Appliquer le fichier CSS
        this.getStylesheets().add(getClass().getResource("/com/ecaf/ecafclientjava/css/theme-clair/tableauFormulaire.css").toExternalForm());
    }

    private void configureUserTableView(TableView<User> tableView) {
        TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.getStyleClass().add("table-column-header");

        TableColumn<User, String> nomColumn = new TableColumn<>("Nom");
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nomColumn.getStyleClass().add("table-column-header");

        TableColumn<User, String> prenomColumn = new TableColumn<>("Prenom");
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        prenomColumn.getStyleClass().add("table-column-header");

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.getStyleClass().add("table-column-header");

        TableColumn<User, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleColumn.getStyleClass().add("table-column-header");

        tableView.getColumns().addAll(idColumn, nomColumn, prenomColumn, emailColumn, roleColumn);
        tableView.getStyleClass().add("table-view");

        // Ensure the table resizes with the window
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        HBox.setHgrow(tableView, Priority.ALWAYS);

        // Set preferred size for the table (optional, can be removed)
        tableView.setPrefWidth(600); // You can adjust the width as needed
        tableView.setPrefHeight(400); // You can adjust the height as needed
    }

    private void fetchAndPopulateUserData() {
        HttpService httpService = new HttpService();
        try {
            List<User> admins = httpService.getUsersByRole("Administrateur");
            List<User> adherents = httpService.getUsersByRole("Adherent");

            ObservableList<User> userData = FXCollections.observableArrayList();
            userData.addAll(admins);
            userData.addAll(adherents);

            FXCollections.sort(userData, Comparator.comparingInt(User::getId));

            userTableView.setItems(userData);

            // Populate responsableIdComboBox with user IDs
            ObservableList<Integer> userIds = FXCollections.observableArrayList();
            for (User user : userData) {
                userIds.add(user.getId());
            }
            FXCollections.sort(userIds);
            responsableIdComboBox.setItems(userIds);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleSubmit() {
        String description = descriptionField.getText();
        LocalDateTime dateDebut = dateDebutPicker.getValue().atStartOfDay();
        LocalDateTime dateFin = dateFinPicker.getValue() != null ? dateFinPicker.getValue().atStartOfDay() : null;
        String statut = statutComboBox.getValue();
        Integer responsableId = responsableIdComboBox.getValue();

        // Add validation and error handling
        if (description.isEmpty() || dateDebut == null || statut == null || responsableId == null) {
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
            System.out.println(responseWrapper.getBody());
            if (Objects.equals(responseWrapper.getStatusCode(), 201)) {
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
        responsableIdComboBox.setValue(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}