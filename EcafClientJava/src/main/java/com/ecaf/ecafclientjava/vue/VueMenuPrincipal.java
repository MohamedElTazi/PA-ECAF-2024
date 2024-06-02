package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.entites.User;
import com.ecaf.ecafclientjava.technique.HttpService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class VueMenuPrincipal extends BorderPane {

    private TableView<User> userTableView = new TableView<>();

    public VueMenuPrincipal() {
        // Configure the TableView
        configureUserTableView();

        // Fetch and populate data
        fetchAndPopulateUserData();

        // Create layout for the table
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(10));
        tableBox.setAlignment(Pos.TOP_RIGHT);
        tableBox.getChildren().add(userTableView);

        // Set preferred size for the table
        userTableView.setPrefSize(300, 200);

        // Add to the BorderPane
        setTop(tableBox);
    }

    private void configureUserTableView() {
        TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.getStyleClass().add("table-column-header");

        TableColumn<User, String> nomColumn = new TableColumn<>("Nom");
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nomColumn.getStyleClass().add("table-column-header");

        TableColumn<User, String> prenomColumn = new TableColumn<>("Prenom");
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        prenomColumn.getStyleClass().add("table-column-header");

        TableColumn<User, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleColumn.getStyleClass().add("table-column-header");

        TableColumn<User, Boolean> onlineColumn = new TableColumn<>("Online");
        onlineColumn.setCellValueFactory(new PropertyValueFactory<>("estEnLigne"));
        onlineColumn.setCellFactory(column -> new TableCell<>() {
            private final Circle circle = new Circle(5);

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    circle.setFill(item ? Color.GREEN : Color.RED);
                    setGraphic(circle);
                }
            }
        });

        userTableView.getColumns().addAll(idColumn, nomColumn, prenomColumn, roleColumn, onlineColumn);
        userTableView.getStyleClass().add("table-view");

        // Ensure the table resizes with the window
        userTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void fetchAndPopulateUserData() {
        HttpService httpService = new HttpService();
        try {
            List<User> onlineUsers = httpService.getUsersByOnlineStatus(); // Assurez-vous que cette méthode existe dans votre HttpService

            ObservableList<User> userData = FXCollections.observableArrayList();
            userData.addAll(onlineUsers);

            FXCollections.sort(userData, Comparator.comparingInt(User::getId));

            userTableView.setItems(userData);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
