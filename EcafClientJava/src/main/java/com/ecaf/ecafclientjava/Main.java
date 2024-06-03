package com.ecaf.ecafclientjava;

import com.ecaf.ecafclientjava.entites.Evenement;
import com.ecaf.ecafclientjava.technique.HttpResponseWrapper;
import com.ecaf.ecafclientjava.technique.HttpService;
import com.ecaf.ecafclientjava.vue.VueCalendrier;
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
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main extends Application {

    private final MenuItem itemConnecter = new MenuItem("Se connecter");
    private final MenuItem itemDeconnecter = new MenuItem("Se deconnecter");
    private JsonNode jsonResponse; // Variable pour stocker la réponse JSON
    private int statusCode; // Variable pour stocker le code de statut
    private final HttpService httpService = new HttpService();

    @Override
    public void start(Stage stage) throws IOException {
        List<Evenement> evenements = new ArrayList<>();
        // Ajout d'événements pour démonstration
        evenements.add(new Evenement(LocalDate.now(), "Réunion"));
        evenements.add(new Evenement(LocalDate.now().plusDays(2), "Anniversaire"));

        VueCalendrier vueCalendrier = new VueCalendrier(evenements);

        Text text = new Text("ECAF Client");
        BorderPane root = new BorderPane();
        root.setTop(createMenuBar());
        root.setCenter(vueCalendrier);

        Scene scene = new Scene(root, 1080, 720);
        stage.setTitle("ECAF Client");
        stage.setScene(scene);
        stage.show();

        itemDeconnecter.setDisable(true);
    }

    private MenuBar createMenuBar() {
        MenuBar barreMenus = new MenuBar();

        MenuItem itemQuitter = new MenuItem("Quitter");
        itemQuitter.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));

        Menu menuFichier = new Menu("Fichier");
        menuFichier.getItems().addAll(itemConnecter, itemDeconnecter, itemQuitter);

        Menu menuRessource = new Menu("Ressource");
        menuRessource.getItems().add(new MenuItem("Gestion des ressources"));

        Menu menuTache = new Menu("Tache");
        menuTache.getItems().addAll(new MenuItem("Gestion des taches"), new MenuItem("Planification des taches"));

        barreMenus.getMenus().addAll(menuFichier, menuRessource, menuTache);

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

                        HttpResponseWrapper responseWrapper = httpService.sendPostRequest("auth/login", requestBody);
                        jsonResponse = responseWrapper.getBody();
                        statusCode = responseWrapper.getStatusCode();

                        System.out.println("Status code: " + statusCode);

                        if (statusCode == 200) {
                            itemDeconnecter.setDisable(false);
                            itemConnecter.setDisable(true);
                            System.out.println("Connexion réussie");

                        } else {
                            VueConnexionEchoue vueErreur = new VueConnexionEchoue();
                            vueErreur.showAndWait();
                        }
                    } catch (IOException | InterruptedException e) {
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
                itemConnecter.setDisable(false);
                itemDeconnecter.setDisable(true);
                System.out.println("Déconnecté");
            }
        });

        itemQuitter.setOnAction(event -> Platform.exit());

        return barreMenus;
    }

    public static void main(String[] args) {
        launch();
    }
}
