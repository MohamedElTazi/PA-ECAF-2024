package com.ecaf.ecafclientjava;

import com.ecaf.ecafclientjava.entites.User;
import com.ecaf.ecafclientjava.technique.HttpResponseWrapper;
import com.ecaf.ecafclientjava.technique.HttpService;
import com.ecaf.ecafclientjava.technique.Session;
import com.ecaf.ecafclientjava.vue.*;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

public class Main extends Application {

    private final MenuItem itemConnecter = new MenuItem("Se connecter");
    private final MenuItem itemDeconnecter = new MenuItem("Se deconnecter");
    private JsonNode jsonResponse;
    private int statusCode;
    private final HttpService httpService = new HttpService();

    @Override
    public void start(Stage stage) throws IOException {

        Text text = new Text("ECAF Client");
        text.setFont(new Font("Arial", 24));
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setStyle("-fx-background-color: #f0f0f0;");
        BorderPane topPane = new BorderPane();

        StackPane centerPane = new StackPane(text);
        centerPane.setPadding(new Insets(20));
        centerPane.setStyle("-fx-background-color: #f0f0f0;");
        root.setCenter(centerPane);

        MenuBar barreMenus = new MenuBar();

        MenuItem itemQuitter = new MenuItem("Quitter");
        itemQuitter.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));

        //////////////////////// Menu Fichier ///////////////////////////

        Menu menuFichier = new Menu("Fichier");
        menuFichier.getItems().addAll(itemConnecter, itemDeconnecter, new SeparatorMenuItem(), itemQuitter);
        barreMenus.getMenus().add(menuFichier);

        //////////////////////// Menu Principal ///////////////////////////
        MenuItem itemMenuPrincipal = new MenuItem("Accueil");
        Menu menuPrincipal = new Menu("Menu Principal");
        menuPrincipal.getItems().add(itemMenuPrincipal);
        barreMenus.getMenus().add(menuPrincipal);

        //////////////////////// Menu Ressource ///////////////////////////
        MenuItem itemCreationRessource = new MenuItem("Ajout d'une ressource");

        MenuItem itemGestionRessources = new MenuItem("Gestion des ressources");
        Menu menuRessource = new Menu("Ressource");
        menuRessource.getItems().addAll(itemGestionRessources, itemCreationRessource);
        barreMenus.getMenus().add(menuRessource);

        //////////////////////// Menu Tache ///////////////////////////

        MenuItem itemGestionTaches = new MenuItem("Gestion des taches");
        MenuItem itemPlanificationTaches = new MenuItem("Planification des taches");
        Menu menuTache = new Menu("Tache");
        menuTache.getItems().addAll(itemGestionTaches, itemPlanificationTaches);
        barreMenus.getMenus().add(menuTache);

        root.setTop(barreMenus);

        Scene scene = new Scene(root, 1080, 720);
        scene.getStylesheets().add(getClass().getResource("/com/ecaf/ecafclientjava/css/theme-clair/main.css").toExternalForm());
        stage.setTitle("ECAF");
        stage.setScene(scene);
        stage.show();

        itemDeconnecter.setDisable(true);
        menuRessource.setDisable(true);
        menuTache.setDisable(true);
        menuPrincipal.setDisable(true);

        itemConnecter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                VueConnexion vue = new VueConnexion();
                Optional<Pair<String, String>> reponse = vue.showAndWait();

                if (reponse.isPresent() && !reponse.get().getKey().isEmpty() && !reponse.get().getValue().isEmpty()) {
                    try {
                        String username = "alice.martin@email.com";
                        String password = "motdepasse2";
                        String requestBody = "{\"email\":\"" + username + "\", \"motDePasse\":\"" + password + "\"}";

                        HttpResponseWrapper responseWrapper = httpService.sendPostRequest("auth/login", requestBody);
                        jsonResponse = responseWrapper.getBody();
                        statusCode = responseWrapper.getStatusCode();

                        if (statusCode == 200) {
                            JsonNode userNode = jsonResponse.get("user");
                            User admin = new User(Integer.parseInt(userNode.get("id").asText()), userNode.get("nom").asText(), userNode.get("prenom").asText(), userNode.get("email").asText(), userNode.get("motDePasse").asText(), userNode.get("role").asText(), Instant.parse(userNode.get("dateInscription").asText()), userNode.get("estBenevole").asBoolean(), jsonResponse.get("token").asText(), false);

                            Session.ouvrir(admin);
                            itemConnecter.setDisable(true);
                            itemDeconnecter.setDisable(false);
                            menuRessource.setDisable(false);
                            menuTache.setDisable(false);
                            menuPrincipal.setDisable(false);

                            VueMenuPrincipal menuPrincipal = new VueMenuPrincipal();
                            root.setCenter(menuPrincipal);
                        } else {
                            VueConnexionEchoue vueEchoue = new VueConnexionEchoue();
                            vueEchoue.showAndWait();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    VueConnexionVide vueVide = new VueConnexionVide();
                    vueVide.showAndWait();
                }
            }
        });

        itemDeconnecter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    User userCourant = Session.getSession().getLeVisiteur();
                    String requestBody = "{\"token\":\"" + userCourant.getToken() + "\"}";
                    HttpResponseWrapper responseWrapper = httpService.sendDeleteRequest("auth/logout/" + userCourant.getId(), requestBody);
                    jsonResponse = responseWrapper.getBody();
                    statusCode = responseWrapper.getStatusCode();

                    if (statusCode == 201) {
                        Session.fermer();
                        itemConnecter.setDisable(false);
                        itemDeconnecter.setDisable(true);
                        menuRessource.setDisable(true);
                        menuTache.setDisable(true);
                        menuPrincipal.setDisable(true);

                        root.setCenter(new Text("Vous êtes déconnecté"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                    try {
                        User userCourant = Session.getSession().getLeVisiteur();
                        String requestBody = "{\"token\":\"" + userCourant.getToken() + "\"}";
                        HttpResponseWrapper responseWrapper = httpService.sendDeleteRequest("auth/logout/" + userCourant.getId(), requestBody);
                        jsonResponse = responseWrapper.getBody();
                        statusCode = responseWrapper.getStatusCode();

                        if (statusCode == 201) {
                            Session.fermer();
                            Platform.exit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    alertQuitter.close();
                }
            }
        });

        itemPlanificationTaches.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        VuePlanificationTache planificationTache = new VuePlanificationTache();

                        root.setCenter(planificationTache);
                    }
                }
        );

        itemGestionTaches.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        VueGestionTache gestionTache = new VueGestionTache();

                        root.setCenter(gestionTache);
                    }
                }
        );

        itemCreationRessource.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        VueAjoutRessource ajoutRessource = new VueAjoutRessource();
                        root.setCenter(ajoutRessource);
                    }
                }
        );

        itemGestionRessources.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        VueGestionRessource gestionRessource = new VueGestionRessource();

                        root.setCenter(gestionRessource);
                    }
                }
        );

        itemMenuPrincipal.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        VueMenuPrincipal menuPrincipal1 = new VueMenuPrincipal();
                        root.setCenter(menuPrincipal1);
                    }
                }
        );

        stage.setOnCloseRequest((EventHandler<WindowEvent>) event -> {
            // Consommer l'événement pour empêcher la fermeture de la fenêtre
            event.consume();

            // Afficher une alerte de confirmation
            Alert alertQuitter = new Alert(Alert.AlertType.CONFIRMATION);
            alertQuitter.setTitle("Quitter");
            alertQuitter.setHeaderText("Demande de confirmation");
            alertQuitter.setContentText("Voulez-vous quitter l'application ?");
            ButtonType btnOui = new ButtonType("Oui");
            ButtonType btnNon = new ButtonType("Non");
            alertQuitter.getButtonTypes().setAll(btnOui, btnNon);

            Optional<ButtonType> reponse = alertQuitter.showAndWait();

            if (reponse.isPresent() && reponse.get() == btnOui) {
                try {
                    User userCourant = Session.getSession().getLeVisiteur();
                    String requestBody = "{\"token\":\"" + userCourant.getToken() + "\"}";
                    HttpResponseWrapper responseWrapper = httpService.sendDeleteRequest("auth/logout/" + userCourant.getId(), requestBody);
                    jsonResponse = responseWrapper.getBody();
                    statusCode = responseWrapper.getStatusCode();

                    if (statusCode == 201) {
                        Session.fermer();
                        Platform.exit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                alertQuitter.close();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}