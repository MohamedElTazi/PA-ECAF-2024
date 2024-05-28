package com.ecaf.ecafclientjava;

import com.ecaf.ecafclientjava.technique.HttpService;
import com.ecaf.ecafclientjava.vue.VueConnexion;

import com.ecaf.ecafclientjava.vue.VueConnexionVide;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
    private Label responseLabel = new Label("Waiting for response...");
    private MenuItem itemConnecter = new MenuItem("Connect");

    public JsonNode jsonResponse; // Variable pour stocker la réponse JSON

    private HttpService httpService = new HttpService(this::handleResponse);
    @Override
    public void start(Stage stage) throws IOException {

        Text text = new Text("ECAF Client");
        BorderPane root = new BorderPane();
        root.setCenter(text);

        MenuBar barreMenus = new MenuBar();

        Menu menuFichier = new Menu("Fichier");

        MenuItem itemConnecter = new MenuItem("Se connecter");
        MenuItem itemDeconnecter = new MenuItem("Se deconnecter");
        MenuItem itemQuitter = new MenuItem("Quitter");

        menuFichier.getItems().add(itemConnecter);
        menuFichier.getItems().add(itemDeconnecter);
        menuFichier.getItems().add(itemQuitter);

        barreMenus.getMenus().add(menuFichier);

        root.setTop(barreMenus);

        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        itemDeconnecter.setDisable(true);

        itemConnecter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                VueConnexion vue = new VueConnexion();
                Optional<Pair<String, String>> reponse = vue.showAndWait();

                if (reponse.isPresent() && !reponse.get().getKey().isEmpty() && !reponse.get().getValue().isEmpty()) {
                    try {
                        String username = reponse.get().getKey();
                        String password = reponse.get().getValue();
                        String requestBody = "{\"email\":\"" + username + "\", \"motDePasse\":\"" + password + "\"}";

                        httpService.sendAsyncPostRequest(requestBody, Main.this::handleResponse);
                        JsonNode jsonNode = getJsonResponse();
                        TimeUnit.SECONDS.sleep(3);

                        System.out.println("OKKKKKKK "+jsonNode);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    VueConnexionVide vueVide = new VueConnexionVide();
                    vueVide.showAndWait();
                }
            }
        });

        itemQuitter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alertQuitter = new Alert(Alert.AlertType.CONFIRMATION);
                alertQuitter.setTitle("Quitter");
                alertQuitter.setHeaderText("Demande de confirmation");
                alertQuitter.setContentText("Voulez-vous quitter l'application ?");
                ButtonType btnOui = new ButtonType("Oui");
                ButtonType btnNon = new ButtonType("Non");
                alertQuitter.getButtonTypes().setAll(btnOui, btnNon);

                Optional<ButtonType> reponse = alertQuitter.showAndWait();

                if (reponse.isPresent() && reponse.get() == btnOui) {
                    Platform.exit();
                } else {
                    alertQuitter.close();
                }
            }
        });
    }

    private void handleResponse(JsonNode response) {
        System.out.println(response);
        this.jsonResponse = response;
    }

    public JsonNode getJsonResponse() {
        return this.jsonResponse; // Méthode pour récupérer la réponse JSON
    }



    public static void main(String[] args) {
        launch();
    }
}
