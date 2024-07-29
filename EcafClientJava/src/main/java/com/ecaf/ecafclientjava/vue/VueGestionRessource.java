package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.entites.Ressource;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;

public class VueGestionRessource extends BorderPane {
    HttpService httpService = new HttpService();
    private JsonNode jsonResponse;
    private int statusCode;
    private final TableView<Ressource> ressourceTableView = new TableView<>();

    public VueGestionRessource() {
        configureRessourceTableView();

        // Fetch and populate data
        fetchAndPopulateRessourceData();

        // Set the layout
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.getChildren().add(ressourceTableView);

        // Add to the BorderPane
        setCenter(vbox);

        // Apply initial CSS
        applyCurrentTheme();
    }

    private void configureRessourceTableView() {
        ressourceTableView.setEditable(true);

        TableColumn<Ressource, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ressourceID"));

        TableColumn<Ressource, String> nomColumn = new TableColumn<>("Nom");
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nomColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nomColumn.setOnEditCommit(event -> {
            Ressource ressource = event.getRowValue();
            ressource.setNom(event.getNewValue());
            handleEditRessource(ressource);
        });

        TableColumn<Ressource, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        typeColumn.setOnEditCommit(event -> {
            Ressource ressource = event.getRowValue();
            ressource.setType(event.getNewValue());
            handleEditRessource(ressource);
        });

        TableColumn<Ressource, Integer> quantiteColumn = new TableColumn<>("Quantité");
        quantiteColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        quantiteColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(Integer object) {
                return object.toString();
            }

            @Override
            public Integer fromString(String string) {
                return Integer.parseInt(string);
            }
        }));
        quantiteColumn.setOnEditCommit(event -> {
            Ressource ressource = event.getRowValue();
            ressource.setQuantite(event.getNewValue());
            handleEditRessource(ressource);
        });

        TableColumn<Ressource, String> emplacementColumn = new TableColumn<>("Emplacement");
        emplacementColumn.setCellValueFactory(new PropertyValueFactory<>("emplacement"));
        emplacementColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emplacementColumn.setOnEditCommit(event -> {
            Ressource ressource = event.getRowValue();
            ressource.setEmplacement(event.getNewValue());
            handleEditRessource(ressource);
        });

        TableColumn<Ressource, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(createButtonCellFactory());

        ressourceTableView.getColumns().addAll(idColumn, nomColumn, typeColumn, quantiteColumn, emplacementColumn, actionColumn);

        // Appliquer la politique de redimensionnement automatique
        ressourceTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        applyTableStyles(ressourceTableView);
    }

    private void applyTableStyles(TableView<?> tableView) {
        tableView.getStyleClass().add("table-view");
        for (TableColumn<?, ?> column : tableView.getColumns()) {
            column.getStyleClass().add("table-column-header");
        }
    }

    private Callback<TableColumn<Ressource, Void>, TableCell<Ressource, Void>> createButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Ressource, Void> call(final TableColumn<Ressource, Void> param) {
                final Button btnDelete = new Button("Delete");
                btnDelete.getStyleClass().add("button-delete");
                final TableCell<Ressource, Void> cell = new TableCell<>() {
                    {
                        btnDelete.setOnAction(event -> {
                            Ressource ressource = getTableView().getItems().get(getIndex());
                            handleDeleteRessource(ressource);
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

    private void fetchAndPopulateRessourceData() {
        try {
            List<Ressource> ressources;
            if (NetworkUtil.isOnline()) {
                ressources = httpService.getAllRessources();
            } else {
                ressources = SQLiteHelper.getAllRessources();
            }

            ObservableList<Ressource> ressourceData = FXCollections.observableList(ressources);

            ressourceTableView.setItems(ressourceData);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleEditRessource(Ressource ressource) {
        if (NetworkUtil.isOnline()) {
            try {
                String requestBody = "{"
                        + "\"nom\":\"" + ressource.getNom() + "\","
                        + "\"type\":\"" + ressource.getType() + "\","
                        + "\"quantite\":" + ressource.getQuantite() + ","
                        + "\"emplacement\":\"" + ressource.getEmplacement() + "\""
                        + "}";

                HttpResponseWrapper responseWrapper = httpService.sendPatchRequest("ressources/" + ressource.getRessourceID(), requestBody);
                jsonResponse = responseWrapper.getBody();
                statusCode = responseWrapper.getStatusCode();
                fetchAndPopulateRessourceData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Update local SQLite database
            SQLiteHelper.updateRessource(ressource);
            fetchAndPopulateRessourceData();
        }
    }

    private void handleDeleteRessource(Ressource ressource) {
        if (NetworkUtil.isOnline()) {
            try {
                HttpResponseWrapper responseWrapper = httpService.sendDeleteRequest("ressources/" + ressource.getRessourceID(), "");
                jsonResponse = responseWrapper.getBody();
                statusCode = responseWrapper.getStatusCode();
                fetchAndPopulateRessourceData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Delete from local SQLite database
            SQLiteHelper.deleteRessource(ressource.getRessourceID());
            fetchAndPopulateRessourceData();
        }
    }

    public void applyCurrentTheme() {
        this.getStylesheets().clear();
        this.getStylesheets().add(getClass().getResource(Theme.themeTableauFormulaire).toExternalForm());
        this.setStyle("-fx-background-color: " + Theme.backgroudColorMain + ";");
    }
}
