package com.ecaf.ecafclientjava;

import com.ecaf.ecafclientjava.entites.AG;
import com.ecaf.ecafclientjava.entites.Evenement;
import com.ecaf.ecafclientjava.entites.Tache;
import com.ecaf.ecafclientjava.entites.User;
import com.ecaf.ecafclientjava.plugins.PluginManager;
import com.ecaf.ecafclientjava.plugins.theme.ThemePlugin;
import com.ecaf.ecafclientjava.technique.*;
import com.ecaf.ecafclientjava.vue.*;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
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
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private final MenuItem itemConnecter = new MenuItem("Se connecter");
    private final MenuItem itemDeconnecter = new MenuItem("Se deconnecter");
    private JsonNode jsonResponse;
    private int statusCode;
    private final HttpService httpService = new HttpService();
    List<Evenement> evenements = httpService.getAllEvenement();
    List<Tache> taches = httpService.getAllTaches();
    List<AG> ags = httpService.getAllAG();
    private Scene scene;
    private BorderPane root = new BorderPane();
    private VueMenuPrincipal vueMenuPrincipal;
    private VueGestionRessource vueGestionRessource;
    private VueAjoutRessource vueAjoutRessource;
    private VueGestionTache vueGestionTache;
    private VuePlanificationTache vuePlanificationTache;
    private static final String VERSION = "version_1.0.0"; // Changez la version pour vérifier la mise à jour
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());



    public Main() throws IOException, InterruptedException {
    }

    @Override
    public void start(Stage stage) throws Exception {
        LOGGER.log(Level.INFO, "Application démarrée, version " + VERSION);

        root.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        root.setId("rootPane");

        MenuBar barreMenus = new MenuBar();

        MenuItem itemQuitter = new MenuItem("Quitter");
        itemQuitter.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));

        // Menu Fichier
        Menu menuFichier = new Menu("Fichier");
        menuFichier.getItems().addAll(itemConnecter, itemDeconnecter, new SeparatorMenuItem(), itemQuitter);
        barreMenus.getMenus().add(menuFichier);

        // Menu Principal
        MenuItem itemMenuPrincipal = new MenuItem("Accueil");
        MenuItem itemCalendrier = new MenuItem("Calendrier");
        Menu menuPrincipal = new Menu("Menu Principal");
        menuPrincipal.getItems().add(itemMenuPrincipal);
        menuPrincipal.getItems().add(itemCalendrier);
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
        stage.setTitle("ECAF - Version Mise à Jour " + VERSION); // Changez le titre pour vérifier la mise à jour
        stage.setScene(scene);
        stage.show();



        itemDeconnecter.setDisable(true);
        menuRessource.setDisable(true);
        menuTache.setDisable(true);
        menuPrincipal.setDisable(true);

        applyCurrentTheme();


        itemModeClair.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
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
                    }
                }
        );
        itemModeSombre.setOnAction(event -> {
            PluginManager pluginManager = null;
            try {
                String pluginsDir = "/home/r-mehdi/ESGI/pa/ECAF-JAR/plugins";
                pluginManager = new PluginManager(pluginsDir);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            for (ThemePlugin plugin : pluginManager.getThemePlugins()) {
                if ("sombre".equals(plugin.getThemeName())) {
                    plugin.applyTheme(scene);
                }
            }
            applyCurrentTheme();
            updateThemesInViews();
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
        itemCalendrier.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        VueCalendrier vueCalendrier = new VueCalendrier(evenements, taches, ags);
                        root.setCenter(vueCalendrier);
                    }
                }
        );

        stage.setOnCloseRequest(event -> {
            event.consume();
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


    private void updateThemesInViews() {
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
    }

    /*private void checkForUpdates() {
        try {
            HttpResponseWrapper httpResponseWrapper = httpService.sendGetRequest("fileVersion");
            jsonResponse = httpResponseWrapper.getBody();
            JsonNode fileVersionNode = jsonResponse.get("fileVersion");
            String fileVersion = fileVersionNode.asText();

            SequentialTransition sequentialTransition = new SequentialTransition();


            if (!CURRENT_VERSION.equals(fileVersion)) {
                sequentialTransition.getChildren().addAll(
                        createAlertWithDelay(AlertType.INFORMATION, "Mise à jour disponible", "Nouvelle version disponible. Téléchargement en cours..."),
                        new PauseTransition(Duration.seconds(1)) // Ajoute un délai avant de lancer le téléchargement
                );

                sequentialTransition.setOnFinished(event -> {
                    try {
                        Runtime.getRuntime().exec("bash update.sh");
                        Platform.exit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            sequentialTransition.play();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la vérification ou de la mise à jour : " + e.getMessage());
        }
    }*/

    private PauseTransition createAlertWithDelay(AlertType alertType, String title, String message) {
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> showAlert(alertType, title, message));
        return pause;
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        LOGGER.log(Level.INFO, "Lancement de l'application, version " + VERSION);
        Application.launch(Main.class, args);
    }
}