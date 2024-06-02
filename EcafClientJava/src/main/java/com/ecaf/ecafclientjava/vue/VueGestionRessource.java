package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.entites.Ressource;
import com.ecaf.ecafclientjava.entites.Tache;
import com.ecaf.ecafclientjava.entites.User;
import com.ecaf.ecafclientjava.technique.HttpResponseWrapper;
import com.ecaf.ecafclientjava.technique.HttpService;
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

public class VueGestionRessource extends BorderPane {
    HttpService httpService = new HttpService();
    private JsonNode jsonResponse;
    private int statusCode;


    private final TableView<Ressource> ressourceTableView = new TableView<>();

    public VueGestionRessource() {
        configureRessourceTableView();


        // Fetch and populate data
        fetchAndPopulateTacheData();

        // Set the layout
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.getChildren().add(ressourceTableView);

        // Add to the BorderPane
        setCenter(vbox);
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
    }



    private Callback<TableColumn<Ressource, Void>, TableCell<Ressource, Void>> createButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Ressource, Void> call(final TableColumn<Ressource, Void> param) {
                final TableCell<Ressource, Void> cell = new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

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


    private void fetchAndPopulateTacheData() {
        HttpService httpService = new HttpService();
        try {
            List<Ressource> ressources = httpService.getAllRessources(); // Assurez-vous que cette méthode existe dans votre HttpService

            ObservableList<Ressource> tacheData = FXCollections.observableList(ressources);

            ressourceTableView.setItems(tacheData);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }



    private void handleEditRessource(Ressource ressource) {
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
            fetchAndPopulateTacheData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteRessource(Ressource ressource) {
        try {
            HttpResponseWrapper responseWrapper = httpService.sendDeleteRequest("ressources/" + ressource.getRessourceID(),"");
            jsonResponse = responseWrapper.getBody();
            statusCode = responseWrapper.getStatusCode();
            fetchAndPopulateTacheData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
