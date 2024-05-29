package com.ecaf.ecafclientjava;

import com.ecaf.ecafclientjava.technique.HttpResponseWrapper;
import com.ecaf.ecafclientjava.technique.HttpService;
import com.ecaf.ecafclientjava.vue.VueConnexion;

import com.ecaf.ecafclientjava.vue.VueConnexionEchoue;
import com.ecaf.ecafclientjava.vue.VueConnexionVide;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.Optional;

public class Main extends Application {

    private final MenuItem itemConnecter = new MenuItem("Se connecter");
    private final MenuItem itemDeconnecter = new MenuItem("Se deconnecter");
    private JsonNode jsonResponse; // Variable pour stocker la réponse JSON
    private int statusCode; // Variable pour stocker le code de statut
    private final HttpService httpService = new HttpService(this::handleResponse);
    @Override
    public void start(Stage stage) throws IOException {

        Text text = new Text("ECAF Client");
        BorderPane root = new BorderPane();
        root.setCenter(text);

        MenuBar barreMenus = new MenuBar();

        Menu menuFichier = new Menu("Fichier");


        MenuItem itemQuitter = new MenuItem("Quitter");

        menuFichier.getItems().add(itemConnecter);
        menuFichier.getItems().add(itemDeconnecter);
        menuFichier.getItems().add(itemQuitter);

        barreMenus.getMenus().add(menuFichier);

        root.setTop(barreMenus);

        Scene scene = new Scene(root, 1080, 720);
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
                        String requestBody =
                                "{\"email\":\"" + username +
                                "\", \"motDePasse\":\"" + password + "\"}";

                        httpService.sendAsyncPostRequest("auth/login",requestBody, Main.this::handleResponse);

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
    private void handleResponse(HttpResponseWrapper responseWrapper) {
        if (responseWrapper != null) {
            this.jsonResponse = responseWrapper.getBody();
            this.statusCode = responseWrapper.getStatusCode();


            Platform.runLater(() -> {
                if (this.statusCode == 200) {
                    itemConnecter.setDisable(true);
                    itemDeconnecter.setDisable(false);
                } else {
                    VueConnexionEchoue vueEchoue = new VueConnexionEchoue();
                    vueEchoue.showAndWait();
                }
            });
        } else {
            System.out.println("An error occurred, no response received.");
        }
        System.out.println(getJsonResponse() + " " + getStatusCode());
    }

    public JsonNode getJsonResponse() {
        return jsonResponse;
    }
    public int getStatusCode() {return statusCode;}


    public static void main(String[] args) {
        launch();
    }
}
