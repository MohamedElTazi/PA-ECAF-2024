package com.ecaf.ecafclientjava;

import com.ecaf.ecafclientjava.entites.User;
import com.ecaf.ecafclientjava.technique.HttpResponseWrapper;
import com.ecaf.ecafclientjava.technique.HttpService;
import com.ecaf.ecafclientjava.technique.Session;
import com.ecaf.ecafclientjava.technique.Theme;
import com.ecaf.ecafclientjava.vue.*;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
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

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Optional;
public class Main extends Application {

    private final MenuItem itemConnecter = new MenuItem("Se connecter");
    private final MenuItem itemDeconnecter = new MenuItem("Se deconnecter");
    private JsonNode jsonResponse;
    private int statusCode;
    private final HttpService httpService = new HttpService();
    private Scene scene;
    private BorderPane root = new BorderPane();
    private VueMenuPrincipal vueMenuPrincipal;
    private VueGestionRessource vueGestionRessource;
    private VueAjoutRessource vueAjoutRessource;
    private VueGestionTache vueGestionTache;
    private VuePlanificationTache vuePlanificationTache;

    private static final String CURRENT_VERSION = "version_1.0.1";
    private static final String UPDATE_URL_TEMPLATE = "https://github.com/username/repo/releases/download/%s/MyApp.jar";

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        checkForUpdates();

        // Code existant pour configurer l'application JavaFX
        Text text = new Text("ECAF ClientJAR");
        text.setFont(new Font("Arial", 24));
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setId("rootPane");

        StackPane centerPane = new StackPane(text);
        centerPane.setPadding(new Insets(20));
        root.setCenter(centerPane);

        MenuBar barreMenus = new MenuBar();

        MenuItem itemQuitter = new MenuItem("Quitter");
        itemQuitter.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));

        // Menu Fichier
        Menu menuFichier = new Menu("Fichier");
        menuFichier.getItems().addAll(itemConnecter, itemDeconnecter, new SeparatorMenuItem(), itemQuitter);
        barreMenus.getMenus().add(menuFichier);

        // Menu Principal
        MenuItem itemMenuPrincipal = new MenuItem("Accueil");
        Menu menuPrincipal = new Menu("Menu Principal");
        menuPrincipal.getItems().add(itemMenuPrincipal);
        barreMenus.getMenus().add(menuPrincipal);

        // Menu Ressource
        MenuItem itemCreationRessource = new MenuItem("Ajout d'une ressource");
        MenuItem itemGestionRessources = new MenuItem("Gestion des ressources");
        Menu menuRessource = new Menu("Ressource");
        menuRessource.getItems().addAll(itemGestionRessources, itemCreationRessource);
        barreMenus.getMenus().add(menuRessource);

        // Menu Tache
        MenuItem itemGestionTaches = new MenuItem("Gestion des taches");
        MenuItem itemPlanificationTaches = new MenuItem("Planification des taches");
        Menu menuTache = new Menu("Tache");
        menuTache.getItems().addAll(itemGestionTaches, itemPlanificationTaches);
        barreMenus.getMenus().add(menuTache);

        // Menu Thème
        Menu menuTheme = new Menu("Thème");
        MenuItem itemModeClair = new MenuItem("Mode clair");
        MenuItem itemModeSombre = new MenuItem("Mode sombre");
        menuTheme.getItems().addAll(itemModeClair, itemModeSombre);
        barreMenus.getMenus().add(menuTheme);

        root.setTop(barreMenus);

        scene = new Scene(root, 1080, 720);
        scene.getStylesheets().add(getClass().getResource(Theme.themeMain).toExternalForm());
        stage.setTitle("ECAF");
        stage.setScene(scene);
        stage.show();

        itemDeconnecter.setDisable(true);
        menuRessource.setDisable(true);
        menuTache.setDisable(true);
        menuPrincipal.setDisable(true);

        applyCurrentTheme(); // Appliquer le thème initial

        itemModeClair.setOnAction(event -> {
            Theme.applyTheme("clair", scene);
            applyCurrentTheme();
            if (vueMenuPrincipal != null) {
                vueMenuPrincipal.applyCurrentTheme();
            }
            if (vueGestionRessource != null) {
                vueGestionRessource.applyCurrentTheme();
            }
            if (vueAjoutRessource != null) {
                vueAjoutRessource.applyCurrentTheme();
            }
            if (vueGestionTache != null) {
                vueGestionTache.applyCurrentTheme();
            }
            if (vuePlanificationTache != null) {
                vuePlanificationTache.applyCurrentTheme();
            }
        });

        itemModeSombre.setOnAction(event -> {
            Theme.applyTheme("sombre", scene);
            applyCurrentTheme();
            if (vueMenuPrincipal != null) {
                vueMenuPrincipal.applyCurrentTheme();
            }
            if (vueGestionRessource != null) {
                vueGestionRessource.applyCurrentTheme();
            }
            if (vueAjoutRessource != null) {
                vueAjoutRessource.applyCurrentTheme();
            }
            if (vueGestionTache != null) {
                vueGestionTache.applyCurrentTheme();
            }
            if (vuePlanificationTache != null) {
                vuePlanificationTache.applyCurrentTheme();
            }
        });

        itemConnecter.setOnAction(event -> {
            VueConnexion vue = new VueConnexion();
            Optional<Pair<String, String>> reponse = vue.showAndWait();

            if (reponse.isPresent()) {
                Pair<String, String> result = reponse.get();
                if (!result.getKey().isEmpty() && !result.getValue().isEmpty()) {
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

                            vueMenuPrincipal = new VueMenuPrincipal();
                            root.setCenter(vueMenuPrincipal);
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

        itemDeconnecter.setOnAction(event -> {
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
        });

        itemQuitter.setOnAction(event -> {
            Alert alertQuitter = new Alert(Alert.AlertType.CONFIRMATION);
            alertQuitter.setTitle("Quitter");
            alertQuitter.setHeaderText("Demande de confirmation");
            alertQuitter.setContentText("Voulez-vous quitter l'application ?");
            ButtonType btnOui = new ButtonType("Oui");
            ButtonType btnNon = new ButtonType("Non");

            alertQuitter.getButtonTypes().setAll(btnOui, btnNon);

            alertQuitter.getDialogPane().lookupButton(btnOui).getStyleClass().add("button-oui");
            alertQuitter.getDialogPane().lookupButton(btnNon).getStyleClass().add("button-non");
            DialogPane dialogPane = alertQuitter.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource(Theme.themeAlert).toExternalForm());
            dialogPane.getStyleClass().add("alert");

            Optional<ButtonType> reponse = alertQuitter.showAndWait();

            if (reponse.isPresent() && reponse.get() == btnOui) {
                if (Session.getSession() == null) {
                    Platform.exit();
                }
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

        itemPlanificationTaches.setOnAction(event -> {
            vuePlanificationTache = new VuePlanificationTache();
            root.setCenter(vuePlanificationTache);
        });

        itemGestionTaches.setOnAction(event -> {
            vueGestionTache = new VueGestionTache();
            root.setCenter(vueGestionTache);
        });

        itemCreationRessource.setOnAction(event -> {
            vueAjoutRessource = new VueAjoutRessource();
            root.setCenter(vueAjoutRessource);
        });

        itemGestionRessources.setOnAction(event -> {
            vueGestionRessource = new VueGestionRessource();
            root.setCenter(vueGestionRessource);
        });

        itemMenuPrincipal.setOnAction(event -> {
            vueMenuPrincipal = new VueMenuPrincipal();
            root.setCenter(vueMenuPrincipal);
        });

        stage.setOnCloseRequest(event -> {
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

            alertQuitter.getDialogPane().lookupButton(btnOui).getStyleClass().add("button-oui");
            alertQuitter.getDialogPane().lookupButton(btnNon).getStyleClass().add("button-non");
            DialogPane dialogPane = alertQuitter.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource(Theme.themeAlert).toExternalForm());
            dialogPane.getStyleClass().add("alert");
            Optional<ButtonType> reponse = alertQuitter.showAndWait();

            if (reponse.isPresent() && reponse.get() == btnOui) {
                if (Session.getSession() == null) {
                    Platform.exit();
                }
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

    private void applyCurrentTheme() {
        root.setStyle("-fx-background-color: " + Theme.backgroudColorMain + ";");
    }

    private void checkForUpdates() {
        try {
            HttpResponseWrapper httpResponseWrapper = httpService.sendGetRequest("fileVersion");
            jsonResponse = httpResponseWrapper.getBody();
            JsonNode fileVersionNode = jsonResponse.get("fileVersion");
            String fileVersion = fileVersionNode.asText();

            System.out.println("Version actuelle : " + CURRENT_VERSION);
            System.out.println("Version disponible : " + fileVersion);

            if (!CURRENT_VERSION.equals(fileVersion)) {
                System.out.println("Nouvelle version disponible. Téléchargement en cours...");
                String updateUrl = String.format(UPDATE_URL_TEMPLATE, fileVersion);

                // Télécharger la mise à jour
                downloadUpdate(updateUrl);
                System.out.println("Mise à jour téléchargée. Redémarrage de l'application...");

                // Remplacer l'ancien JAR par le nouveau et redémarrer l'application
                Runtime.getRuntime().exec("java -jar update.jar");
                System.exit(0);
            } else {
                System.out.println("L'application est à jour.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadUpdate(String updateUrl) throws IOException {
        URL url = new URL(updateUrl);
        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream("update.jar")) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}