package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.entites.Tache;
import com.ecaf.ecafclientjava.entites.User;
import com.ecaf.ecafclientjava.technique.HttpResponseWrapper;
import com.ecaf.ecafclientjava.technique.HttpService;
import com.ecaf.ecafclientjava.technique.InstantStringConverter;
import com.ecaf.ecafclientjava.technique.Session;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.converter.LocalDateStringConverter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class VueGestionTache extends BorderPane {
    HttpService httpService = new HttpService();
    private JsonNode jsonResponse;
    private int statusCode;
    private TableView<User> adminTableView = new TableView<>();
    private TableView<User> adherentTableView = new TableView<>();

    private TableView<Tache> tacheTableView = new TableView<>();

    public VueGestionTache() {
        // Configure the TableView
        configureTacheTableView();
        configureUserTableView(adherentTableView);
        configureUserTableView(adminTableView);

        // Fetch and populate data
        fetchAndPopulateTacheData();

        fetchAndPopulateUserData();

        // Set the layout
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.getChildren().addAll(tacheTableView,adherentTableView,adminTableView);

        // Add to the BorderPane
        setCenter(vbox);
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

    private void configureTacheTableView() {
        tacheTableView.setEditable(true);

        TableColumn<Tache, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("tacheID"));

        TableColumn<Tache, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(event -> {
            Tache tache = event.getRowValue();
            tache.setDescription(event.getNewValue());
            // Optionally, update the task in the backend here
        });

        TableColumn<Tache, Instant> dateDebutColumn = new TableColumn<>("Date Debut");
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateDebutColumn.setCellFactory(TextFieldTableCell.forTableColumn(new InstantStringConverter()));
        dateDebutColumn.setOnEditCommit(event -> {
            Tache tache = event.getRowValue();
            tache.setDateDebut(event.getNewValue());
            // Optionally, update the task in the backend here
        });

        TableColumn<Tache, Instant> dateFinColumn = new TableColumn<>("Date Fin");
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        dateFinColumn.setCellFactory(TextFieldTableCell.forTableColumn(new InstantStringConverter()));
        dateFinColumn.setOnEditCommit(event -> {
            Tache tache = event.getRowValue();
            tache.setDateFin(event.getNewValue());
            // Optionally, update the task in the backend here
        });

        TableColumn<Tache, String> statutColumn = new TableColumn<>("Statut");
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        statutColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        statutColumn.setOnEditCommit(event -> {
            Tache tache = event.getRowValue();
            tache.setStatut(event.getNewValue());
            // Optionally, update the task in the backend here
        });

        TableColumn<Tache, String> responsableColumn = new TableColumn<>("Responsable");
        responsableColumn.setCellValueFactory(cellData -> {
            User responsable = cellData.getValue().getResponsable();
            return new SimpleStringProperty(responsable != null ? responsable.getNom() + " " + responsable.getPrenom() : "N/A");
        });

        TableColumn<Tache, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(createButtonCellFactory());

        tacheTableView.getColumns().addAll(idColumn, descriptionColumn, dateDebutColumn, dateFinColumn, statutColumn, responsableColumn, actionColumn);
    }

    private Callback<TableColumn<Tache, Void>, TableCell<Tache, Void>> createButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Tache, Void> call(final TableColumn<Tache, Void> param) {
                final TableCell<Tache, Void> cell = new TableCell<>() {
                    private final Button btnEdit = new Button("Edit");
                    private final Button btnDelete = new Button("Delete");

                    {
                        btnEdit.setOnAction(event -> {
                            Tache tache = getTableView().getItems().get(getIndex());
                            handleEditTache(tache);
                        });

                        btnDelete.setOnAction(event -> {
                            Tache tache = getTableView().getItems().get(getIndex());
                            handleDeleteTache(tache);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hBox = new HBox(10, btnEdit, btnDelete);
                            setGraphic(hBox);
                        }
                    }
                };
                return cell;
            }
        };
    }

    private void fetchAndPopulateTacheData() {
        HttpService httpService = new HttpService();
        try {
            List<Tache> taches = httpService.getAllTaches(); // Assurez-vous que cette méthode existe dans votre HttpService

            ObservableList<Tache> tacheData = FXCollections.observableList(taches);

            tacheTableView.setItems(tacheData);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
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

    private void handleEditTache(Tache tache) {
        // Logique pour modifier la tâche
        System.out.println("Modifier tâche: " + tache);
    }

    private void handleDeleteTache(Tache tache) {
        try {
            HttpResponseWrapper responseWrapper = httpService.sendDeleteRequest("taches/"+tache.getTacheID());
            jsonResponse = responseWrapper.getBody();
            statusCode = responseWrapper.getStatusCode();
            fetchAndPopulateTacheData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


