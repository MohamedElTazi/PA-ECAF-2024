package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.entites.Ressource;
import com.ecaf.ecafclientjava.entites.Tache;
import com.ecaf.ecafclientjava.entites.User;
import com.ecaf.ecafclientjava.technique.HttpResponseWrapper;
import com.ecaf.ecafclientjava.technique.HttpService;
import com.ecaf.ecafclientjava.technique.sqllite.NetworkUtil;
import com.ecaf.ecafclientjava.technique.Theme;
import com.ecaf.ecafclientjava.technique.sqllite.SQLiteHelper;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VueGestionTache extends BorderPane {
    HttpService httpService = new HttpService();
    private JsonNode jsonResponse;
    private int statusCode;
    private TableView<User> userTableView = new TableView<>();
    private TableView<Tache> tacheTableView = new TableView<>();
    private TableView<Ressource> ressourceTableView = new TableView<>();

    public VueGestionTache() {
        // Configure the TableView
        configureTacheTableView();
        configureUserTableView(userTableView);
        configureRessourceTableView(ressourceTableView);

        // Fetch and populate data
        fetchAndPopulateTacheData();
        fetchAndPopulateUserData();
        fetchAndPopulateRessourceData();

        // Set the layout
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.getChildren().addAll(tacheTableView, userTableView, ressourceTableView);

        // Add to the BorderPane
        setCenter(vbox);

        // Apply initial CSS
        applyCurrentTheme();
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

        // Appliquer la politique de redimensionnement automatique
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        applyTableStyles(tableView);
    }

    private void configureRessourceTableView(TableView<Ressource> tableView) {
        TableColumn<Ressource, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ressourceID"));

        TableColumn<Ressource, String> nomColumn = new TableColumn<>("Nom");
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Ressource, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        tableView.getColumns().addAll(idColumn, nomColumn, typeColumn);

        // Appliquer la politique de redimensionnement automatique
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        applyTableStyles(tableView);
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
            handleEditTache(tache);
        });

        TableColumn<Tache, Instant> dateDebutColumn = new TableColumn<>("Date Debut");
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateDebutColumn.setCellFactory(param -> new DatePickerCell(true));
        dateDebutColumn.setOnEditCommit(event -> {
            Tache tache = event.getRowValue();
            tache.setDateDebut(event.getNewValue());
            handleEditTache(tache);
        });

        TableColumn<Tache, Instant> dateFinColumn = new TableColumn<>("Date Fin");
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        dateFinColumn.setCellFactory(param -> new DatePickerCell(false));
        dateFinColumn.setOnEditCommit(event -> {
            Tache tache = event.getRowValue();
            tache.setDateFin(event.getNewValue());
            handleEditTache(tache);
        });

        TableColumn<Tache, String> statutColumn = new TableColumn<>("Statut");
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        statutColumn.setCellFactory(ComboBoxTableCell.forTableColumn("En cours", "Fini"));
        statutColumn.setOnEditCommit(event -> {
            Tache tache = event.getRowValue();
            tache.setStatut(event.getNewValue());
            handleEditTache(tache);
        });

        TableColumn<Tache, String> idResponsableColumn = new TableColumn<>("ID Responsable");
        idResponsableColumn.setCellValueFactory(cellData -> {
            User responsable = cellData.getValue().getResponsable();
            return new SimpleStringProperty(responsable != null ? String.valueOf(responsable.getId()) : "N/A");
        });
        idResponsableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        idResponsableColumn.setOnEditCommit(event -> {
            Tache tache = event.getRowValue();
            User responsable = new User();
            responsable.setId(Integer.parseInt(event.getNewValue()));
            tache.setResponsable(responsable);
            handleEditTache(tache);
        });

        TableColumn<Tache, String> responsableColumn = new TableColumn<>("Responsable");
        responsableColumn.setCellValueFactory(cellData -> {
            User responsable = cellData.getValue().getResponsable();
            return new SimpleStringProperty(responsable != null ? responsable.getNom() + " " + responsable.getPrenom() : "N/A");
        });

        TableColumn<Tache, String> idRessourceColumn = new TableColumn<>("ID Ressource");
        idRessourceColumn.setCellValueFactory(cellData -> {
            Ressource ressource = cellData.getValue().getRessource();
            return new SimpleStringProperty(ressource != null ? String.valueOf(ressource.getRessourceID()) : "N/A");
        });
        idRessourceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        idRessourceColumn.setOnEditCommit(event -> {
            Tache tache = event.getRowValue();
            Ressource ressource = new Ressource();
            ressource.setRessourceID(Integer.parseInt(event.getNewValue()));
            tache.setRessource(ressource);
            handleEditTache(tache);
        });

        TableColumn<Tache, String> ressourceColumn = new TableColumn<>("Ressource");
        ressourceColumn.setCellValueFactory(cellData -> {
            Ressource ressource = cellData.getValue().getRessource();
            return new SimpleStringProperty(ressource != null ? ressource.getNom() : "N/A");
        });

        TableColumn<Tache, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(createButtonCellFactory());

        tacheTableView.getColumns().addAll(idColumn, descriptionColumn, dateDebutColumn, dateFinColumn, statutColumn, idResponsableColumn, responsableColumn, idRessourceColumn, ressourceColumn, actionColumn);

        tacheTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        applyTableStyles(tacheTableView);
    }

    private void applyTableStyles(TableView<?> tableView) {
        tableView.getStyleClass().add("table-view");
        for (TableColumn<?, ?> column : tableView.getColumns()) {
            column.getStyleClass().add("table-column-header");
        }
    }

    private Callback<TableColumn<Tache, Void>, TableCell<Tache, Void>> createButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Tache, Void> call(final TableColumn<Tache, Void> param) {
                Button btnDelete = new Button("Delete");
                btnDelete.getStyleClass().add("button-delete");
                final TableCell<Tache, Void> cell = new TableCell<>() {
                    {
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
                            HBox hBox = new HBox(10, btnDelete);
                            setGraphic(hBox);
                        }
                    }
                };
                return cell;
            }
        };
    }

    private void fetchAndPopulateTacheData() {
        try {
            List<Tache> taches = httpService.getAllTaches();

            ObservableList<Tache> tacheData = FXCollections.observableList(taches);

            tacheTableView.setItems(tacheData);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void fetchAndPopulateUserData() {
        try {
            List<User> admins = httpService.getUsersByRole("Administrateur");
            List<User> adherents = httpService.getUsersByRole("Adherent");

            ObservableList<User> userData = FXCollections.observableArrayList();
            userData.addAll(admins);
            userData.addAll(adherents);

            userTableView.setItems(userData);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void fetchAndPopulateRessourceData() {
        try {
            List<Ressource> ressources = httpService.getAllRessources(); // Assurez-vous que cette méthode existe dans votre HttpService

            ObservableList<Ressource> ressourceData = FXCollections.observableList(ressources);

            ressourceTableView.setItems(ressourceData);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleEditTache(Tache tache) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
            String requestBody = "{"
                    + "\"description\":\"" + tache.getDescription() + "\","
                    + "\"dateDebut\":\"" + formatter.format(tache.getDateDebut()) + "\","
                    + "\"dateFin\":\"" + formatter.format(tache.getDateFin()) + "\","
                    + "\"statut\":\"" + tache.getStatut() + "\","
                    + "\"responsable\":" + tache.getResponsable().getId() + ","
                    + "\"ressource\":" + tache.getRessource().getRessourceID()
                    + "}";

            if (NetworkUtil.isOnline()) {
                HttpResponseWrapper responseWrapper = httpService.sendPatchRequest("taches/" + tache.getTacheID(), requestBody);
                jsonResponse = responseWrapper.getBody();
                statusCode = responseWrapper.getStatusCode();
            } else {
                SQLiteHelper.updateTache(tache);
            }
            fetchAndPopulateTacheData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteTache(Tache tache) {
        try {
            if (NetworkUtil.isOnline()) {
                HttpResponseWrapper responseWrapper = httpService.sendDeleteRequest("taches/" + tache.getTacheID(), "");
                jsonResponse = responseWrapper.getBody();
                statusCode = responseWrapper.getStatusCode();
            } else {
                SQLiteHelper.deleteTache(tache.getTacheID());
            }
            fetchAndPopulateTacheData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Custom DatePicker cell factory for Instant
    class DatePickerCell extends TableCell<Tache, Instant> {
        private final DatePicker datePicker = new DatePicker();
        private final boolean isDateDebut;

        DatePickerCell(boolean isDateDebut) {
            this.isDateDebut = isDateDebut;

            datePicker.setConverter(new StringConverter<>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                @Override
                public String toString(LocalDate date) {
                    if (date != null) {
                        return formatter.format(date);
                    } else {
                        return "";
                    }
                }

                @Override
                public LocalDate fromString(String string) {
                    if (string != null && !string.isEmpty()) {
                        return LocalDate.parse(string, formatter);
                    } else {
                        return null;
                    }
                }
            });

            datePicker.setOnAction(event -> {
                if (getTableRow() != null && getTableRow().getItem() != null) {
                    Tache tache = getTableRow().getItem();
                    Instant instant = datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant();
                    if (isDateDebut) {
                        tache.setDateDebut(instant);
                        commitEdit(instant);
                    } else {
                        tache.setDateFin(instant);
                        commitEdit(instant);
                    }
                    handleEditTache(tache);
                }
            });

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(datePicker);
        }

        @Override
        protected void updateItem(Instant item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                datePicker.setValue(LocalDate.ofInstant(item, ZoneId.systemDefault()));
                setGraphic(datePicker);
            }
        }
    }


    public void applyCurrentTheme() {
        this.getStylesheets().clear();
        this.getStylesheets().add(getClass().getResource(Theme.themeTableauFormulaire).toExternalForm());
        this.setStyle("-fx-background-color: " + Theme.backgroudColorMain + ";");
    }
}
