package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.entites.Ressource;
import com.ecaf.ecafclientjava.entites.Tache;
import com.ecaf.ecafclientjava.entites.User;
import com.ecaf.ecafclientjava.technique.HttpResponseWrapper;
import com.ecaf.ecafclientjava.technique.HttpService;
import com.ecaf.ecafclientjava.technique.sqllite.NetworkUtil;
import com.ecaf.ecafclientjava.technique.Theme;
import com.ecaf.ecafclientjava.technique.sqllite.SQLiteHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class VuePlanificationTache extends Pane {

    private TextField descriptionField = new TextField();
    private DatePicker dateDebutPicker = new DatePicker();
    private DatePicker dateFinPicker = new DatePicker();
    private ComboBox<String> statutComboBox = new ComboBox<>();
    private ComboBox<Integer> responsableIdComboBox = new ComboBox<>();
    private ComboBox<Integer> ressourceIdComboBox = new ComboBox<>();
    private Button submitButton = new Button("Submit");
    private Button resetButton = new Button("Reset");

    private TableView<User> userTableView = new TableView<>();
    private TableView<Ressource> ressourceTableView = new TableView<>();

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

        Label ressourceIdLabel = new Label("Ressource ID:");
        ressourceIdLabel.getStyleClass().add("label");
        GridPane.setConstraints(ressourceIdLabel, 0, 5);
        GridPane.setConstraints(ressourceIdComboBox, 1, 5);
        ressourceIdComboBox.getStyleClass().add("combo-box");

        GridPane.setConstraints(submitButton, 1, 6);
        submitButton.getStyleClass().add("button-submit");
        GridPane.setConstraints(resetButton, 2, 6);
        resetButton.getStyleClass().add("button-reset");

        submitButton.setOnAction(e -> handleSubmit());
        resetButton.setOnAction(e -> handleReset());

        grid.getChildren().addAll(descriptionLabel, descriptionField, dateDebutLabel, dateDebutPicker,
                dateFinLabel, dateFinPicker, statutLabel, statutComboBox, responsableIdLabel, responsableIdComboBox,
                ressourceIdLabel, ressourceIdComboBox, submitButton, resetButton);

        // Adding border to the grid
        grid.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT)));

        // VBox for tables
        VBox tableBox = new VBox(0); // Adjust the spacing between elements
        tableBox.setPadding(new Insets(10, 10, 10, 10)); // Adjust the padding to move elements higher
        tableBox.getStyleClass().add("vbox");

        Label userLabel = new Label("Users:");
        userLabel.getStyleClass().add("label");
        configureUserTableView(userTableView);
        tableBox.getChildren().addAll(userLabel, userTableView);

        Label ressourceLabel = new Label("Ressources:");
        ressourceLabel.getStyleClass().add("label");
        configureRessourceTableView(ressourceTableView);
        tableBox.getChildren().addAll(ressourceLabel, ressourceTableView);

        // Main layout with HBox
        HBox mainLayout = new HBox(0);
        mainLayout.setPadding(new Insets(40, 40, 0, 40));
        mainLayout.getStyleClass().add("hbox");
        mainLayout.getChildren().addAll(grid, tableBox);

        // Fetch and populate data
        fetchAndPopulateUserData();
        fetchAndPopulateRessourceData();

        getChildren().add(mainLayout);

        // Appliquer le fichier CSS
        applyCurrentTheme();
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
        tableView.setPrefHeight(200); // Adjust the height if needed
    }

    private void configureRessourceTableView(TableView<Ressource> tableView) {
        TableColumn<Ressource, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ressourceID"));
        idColumn.getStyleClass().add("table-column-header");

        TableColumn<Ressource, String> nomColumn = new TableColumn<>("Nom");
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nomColumn.getStyleClass().add("table-column-header");

        TableColumn<Ressource, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.getStyleClass().add("table-column-header");

        tableView.getColumns().addAll(idColumn, nomColumn, typeColumn);
        tableView.getStyleClass().add("table-view");

        // Ensure the table resizes with the window
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        HBox.setHgrow(tableView, Priority.ALWAYS);

        // Set preferred size for the table (optional, can be removed)
        tableView.setPrefWidth(600); // You can adjust the width as needed
        tableView.setPrefHeight(200); // Adjust the height if needed
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

    private void fetchAndPopulateRessourceData() {
        HttpService httpService = new HttpService();
        try {
            List<Ressource> ressources = httpService.getAllRessources(); // Assurez-vous que cette méthode existe dans votre HttpService

            ObservableList<Ressource> ressourceData = FXCollections.observableArrayList();
            ressourceData.addAll(ressources);

            FXCollections.sort(ressourceData, Comparator.comparingInt(Ressource::getRessourceID));

            ressourceTableView.setItems(ressourceData);

            // Populate ressourceIdComboBox with ressource IDs
            ObservableList<Integer> ressourceIds = FXCollections.observableArrayList();
            for (Ressource ressource : ressourceData) {
                ressourceIds.add(ressource.getRessourceID());
            }
            FXCollections.sort(ressourceIds);
            ressourceIdComboBox.setItems(ressourceIds);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleSubmit() {
        if (descriptionField.getText().isEmpty() || dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null || statutComboBox.getValue() == null || responsableIdComboBox.getValue() == null || ressourceIdComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur Formulaire!", "Veuillez remplir tous les champs du formulaire.");
            return;
        }

        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();
        LocalDate today = LocalDate.now();

        if (!areDatesValid(dateDebut, dateFin, today)) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Date!", "La date de début doit être antérieure à la date de fin et ne peut pas être antérieure à la date d'aujourd'hui.");
            return;
        }

        String description = descriptionField.getText();
        LocalDateTime dateDebutTime = dateDebut.atStartOfDay();
        LocalDateTime dateFinTime = dateFin != null ? dateFin.atStartOfDay() : null;
        String statut = statutComboBox.getValue();
        Integer responsableId = responsableIdComboBox.getValue();
        Integer ressourceId = ressourceIdComboBox.getValue();

        // Add validation and error handling

        // Format dates for database
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateDebutStr = dateDebutTime.format(formatter);
        String dateFinStr = dateFinTime != null ? dateFinTime.format(formatter) : null;

        // Prepare JSON or any other format to send to backend
        String requestBody = String.format("{\"description\":\"%s\", \"dateDebut\":\"%s\", \"dateFin\":\"%s\", \"statut\":\"%s\", \"responsable\":%d, \"ressource\":%d}",
                description, dateDebutStr, dateFinStr, statut, responsableId, ressourceId);

        try {
            if (NetworkUtil.isOnline()) {
                // Send request to backend if online
                HttpService httpService = new HttpService();
                HttpResponseWrapper responseWrapper = httpService.sendPostRequest("taches", requestBody);
                System.out.println(responseWrapper.getBody());
                if (Objects.equals(responseWrapper.getStatusCode(), 201)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Tache créé avec succès !");
                    handleReset();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "\n" + "Échec de l'ajout d'une tache.");
                }
            } else {
                Random random = new Random();
                int randomNumber = random.nextInt(1000);
                Tache tache = new Tache(randomNumber, description, dateDebutTime.toInstant(ZoneOffset.UTC), dateFinTime.toInstant(ZoneOffset.UTC), statut, responsableId, ressourceId);
                SQLiteHelper.createTache(tache);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Tache enregistré localement !");
                handleReset();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Une erreur est survenue !");
        }
    }

    private boolean areDatesValid(LocalDate dateDebut, LocalDate dateFin, LocalDate today) {
        return (dateDebut.isEqual(today) || dateDebut.isAfter(today)) && (dateDebut.isBefore(dateFin) || dateDebut.isEqual(dateFin));
    }

    private void handleReset() {
        descriptionField.clear();
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        statutComboBox.setValue(null);
        responsableIdComboBox.setValue(null);
        ressourceIdComboBox.setValue(null);
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
