package com.ecaf.ecafclientjava;

import com.ecaf.ecafclientjava.entites.AG;
import com.ecaf.ecafclientjava.entites.Evenement;
import com.ecaf.ecafclientjava.entites.Tache;
import com.ecaf.ecafclientjava.entites.User;
import com.ecaf.ecafclientjava.plugins.calculatrice.CalculatorPlugin;
import com.ecaf.ecafclientjava.plugins.PluginManager;
import com.ecaf.ecafclientjava.plugins.kanban.KanbanPlugin;
import com.ecaf.ecafclientjava.plugins.note.NotePlugin;
import com.ecaf.ecafclientjava.plugins.theme.ThemePlugin;
import com.ecaf.ecafclientjava.technique.*;
import com.ecaf.ecafclientjava.technique.sqllite.NetworkUtil;
import com.ecaf.ecafclientjava.technique.sqllite.SQLiteHelper;
import com.ecaf.ecafclientjava.technique.sqllite.SyncManager;
import com.ecaf.ecafclientjava.vue.*;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import javafx.scene.control.Alert.AlertType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
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
    private static final String VERSION = "version_1.0.0";
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
        menuPrincipal.getItems().addAll(itemMenuPrincipal, itemCalendrier);
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
        MenuItem itemModeBlue = new MenuItem("Mode Blue");
        MenuItem itemModeNature = new MenuItem("Mode Nature");
        MenuItem itemModeViolet = new MenuItem("Mode Violet");
        menuTheme.getItems().addAll(itemModeClair, itemModeSombre, itemModeBlue, itemModeNature, itemModeViolet);
        barreMenus.getMenus().add(menuTheme);

        // Menu Plugins
        Menu menuPlugins = new Menu("Plugins");
        MenuItem menuCalculatrice = new MenuItem("Calculatrice");
        menuCalculatrice.setOnAction(event -> {
            CalculatorPlugin calculatorPlugin = loadCalculatorPlugin();
            if (calculatorPlugin != null) {
                Pane calculatorUI = calculatorPlugin.getUI();

                Stage calculatorStage = new Stage();
                calculatorStage.setTitle("Calculatrice");

                Scene calculatorScene = new Scene(calculatorUI, 300, 400);
                calculatorStage.setScene(calculatorScene);
                calculatorStage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le plugin de calculatrice.");
            }
        });
        menuPlugins.getItems().add(menuCalculatrice);
        MenuItem menuNote = new MenuItem("Bloc-notes");
        menuNote.setOnAction(event -> {
            NotePlugin notePlugin = loadNotePlugin();
            if (notePlugin != null) {
                Pane noteUI = notePlugin.getUI();

                Stage noteStage = new Stage();
                noteStage.setTitle("Bloc-notes");

                Scene noteScene = new Scene(noteUI, 600, 400);
                noteStage.setScene(noteScene);
                noteStage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le plugin de bloc-notes.");
            }
        });
        menuPlugins.getItems().add(menuNote);
        // Menu Kanban
        MenuItem menuKanban = new MenuItem("Kanban");
        menuKanban.setOnAction(event -> {
            KanbanPlugin kanbanPlugin = loadKanbanPlugin();
            if (kanbanPlugin != null) {
                Pane kanbanUI = kanbanPlugin.getUI();

                Stage kanbanStage = new Stage();
                kanbanStage.setTitle("Kanban");

                Scene kanbanScene = new Scene(kanbanUI, 800, 600);
                kanbanStage.setScene(kanbanScene);
                kanbanStage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le plugin Kanban.");
            }
        });

        menuPlugins.getItems().add(menuKanban);

        barreMenus.getMenus().add(menuPlugins);

        // Menu Maj
        Menu menuMaj = new Menu("Mise à jour");
        MenuItem itemMaj = new MenuItem("Mettre à jour l'application");
        itemMaj.setOnAction(event -> checkForUpdates());
        menuMaj.getItems().add(itemMaj);
        barreMenus.getMenus().add(menuMaj);

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

        SQLiteHelper.createTables();

        // Vérifiez la connexion et synchronisez les données si en ligne
        boolean isOnline = NetworkUtil.isOnline();
        System.out.println("Network status at startup: " + isOnline);

        if (isOnline) {
            new SyncManager().syncDisplayedTables();
        }

        // Initialiser les listes de données avec gestion des exceptions pour le mode offline
        try {
            evenements = httpService.getAllEvenement();
            taches = httpService.getAllTaches();
            ags = httpService.getAllAG();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            evenements = SQLiteHelper.getAllEvenement();
            taches = SQLiteHelper.getAllTaches();
            ags = SQLiteHelper.getAllAG();
        }

        // Vérification périodique de la connexion
        System.out.println("Initializing Timeline for periodic network check...");

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(60), event -> {
            System.out.println("Checking network status...");
            boolean onlineStatus = NetworkUtil.isOnline();
            System.out.println("Network status during periodic check: " + onlineStatus);

            if (onlineStatus) {
                System.out.println("Network is online. Synchronizing data...");
                // Synchroniser les données
                new SyncManager().syncDisplayedTables();
                new SyncManager().syncData(); // Synchronisation pour ressource et tache
            } else {
                System.out.println("Network is offline. Skipping synchronization.");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        System.out.println("Timeline started.");


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

        EventHandler<ActionEvent> themeEventHandler = event -> {
            MenuItem source = (MenuItem) event.getSource();
            String themeName = source.getText().toLowerCase().replace("mode ", "");

            PluginManager pluginManager;
            try {
                String pluginsDir = "/home/r-mehdi/ESGI/pa/ECAF-JAR/plugins";
                pluginManager = new PluginManager(pluginsDir);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            for (ThemePlugin plugin : pluginManager.getThemePlugins()) {
                if (themeName.equals(plugin.getThemeName())) {
                    plugin.applyTheme(scene);
                }
            }
            applyCurrentTheme();
            updateThemesInViews();
        };

        itemModeSombre.setOnAction(themeEventHandler);
        itemModeBlue.setOnAction(themeEventHandler);
        itemModeNature.setOnAction(themeEventHandler);
        itemModeViolet.setOnAction(themeEventHandler);

        itemConnecter.setOnAction(event -> {
            VueConnexion vue = new VueConnexion();
            Optional<Pair<String, String>> reponse = vue.showAndWait();

            if (reponse.isPresent()) {
                Pair<String, String> result = reponse.get();
                if (!result.getKey().isEmpty() && !result.getValue().isEmpty()) {
                    try {
                        String username = result.getKey();
                        String password = result.getValue();
                        String requestBody = "{\"email\":\"" + username + "\", \"motDePasse\":\"" + password + "\"}";

                        if (NetworkUtil.isOnline()) {
                            // Connexion en ligne
                            HttpResponseWrapper responseWrapper = httpService.sendPostRequest("auth/login", requestBody);
                            jsonResponse = responseWrapper.getBody();
                            statusCode = responseWrapper.getStatusCode();

                            if (statusCode == 200) {
                                JsonNode userNode = jsonResponse.get("user");
                                User admin = new User(
                                        Integer.parseInt(userNode.get("id").asText()),
                                        userNode.get("nom").asText(),
                                        userNode.get("prenom").asText(),
                                        userNode.get("email").asText(),
                                        userNode.get("motDePasse").asText(),
                                        userNode.get("role").asText(),
                                        Instant.parse(userNode.get("dateInscription").asText()),
                                        userNode.get("estBenevole").asBoolean(),
                                        jsonResponse.get("token").asText(),
                                        false
                                );
                                if (!Objects.equals(admin.getRole(), "Administrateur")) {
                                    VueConnexionEchoue vueEchoue = new VueConnexionEchoue();
                                    vueEchoue.showAndWait();
                                    return;
                                }
                                // Enregistrer l'utilisateur dans SQLite pour une utilisation hors ligne
                                SQLiteHelper.saveUser(admin);

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
                        } else {
                            // Connexion hors ligne
                            User user = SQLiteHelper.getUserByEmail(username);
                            String salt = "$2b$10$F4zka3au8kLQG1rAD2Oq5e";
                            String hashedPassword = BCrypt.hashpw(password, salt);
                            if (user != null && user.getMotDePasse().equals(hashedPassword)) {
                                if (!Objects.equals(user.getRole(), "Administrateur")) {
                                    VueConnexionEchoue vueEchoue = new VueConnexionEchoue();
                                    vueEchoue.showAndWait();
                                    return;
                                }
                                Session.ouvrir(user);
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
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        VueConnexionEchoue vueEchoue = new VueConnexionEchoue();
                        vueEchoue.showAndWait();
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

                if (NetworkUtil.isOnline()) {
                    // Déconnexion en ligne
                    String requestBody = "{\"token\":\"" + userCourant.getToken() + "\"}";
                    HttpResponseWrapper responseWrapper = httpService.sendDeleteRequest("auth/logout/" + userCourant.getId(), requestBody);
                    jsonResponse = responseWrapper.getBody();
                    statusCode = responseWrapper.getStatusCode();

                    if (statusCode == 201) {
                        System.out.println("Déconnexion réussie du serveur.");
                    } else {
                        System.out.println("Échec de la déconnexion du serveur.");
                    }
                } else {
                    System.out.println("Déconnexion hors ligne.");
                }

                // Fermer la session localement dans les deux cas (en ligne et hors ligne)
                Session.fermer();
                itemConnecter.setDisable(false);
                itemDeconnecter.setDisable(true);
                menuRessource.setDisable(true);
                menuTache.setDisable(true);
                menuPrincipal.setDisable(true);

                root.setCenter(new Text("Vous êtes déconnecté"));

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
                } else {
                    try {
                        User userCourant = Session.getSession().getLeVisiteur();

                        if (NetworkUtil.isOnline()) {
                            // Déconnexion en ligne
                            String requestBody = "{\"token\":\"" + userCourant.getToken() + "\"}";
                            HttpResponseWrapper responseWrapper = httpService.sendDeleteRequest("auth/logout/" + userCourant.getId(), requestBody);
                            jsonResponse = responseWrapper.getBody();
                            statusCode = responseWrapper.getStatusCode();

                            if (statusCode == 201) {
                                System.out.println("Déconnexion réussie du serveur.");
                            } else {
                                System.out.println("Échec de la déconnexion du serveur.");
                            }
                        } else {
                            System.out.println("Déconnexion hors ligne.");
                        }

                        // Fermer la session localement dans les deux cas (en ligne et hors ligne)
                        Session.fermer();
                        Platform.exit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        itemCalendrier.setOnAction(event -> {
            VueCalendrier vueCalendrier = new VueCalendrier(evenements, taches, ags);
            root.setCenter(vueCalendrier);
        });

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
                } else {
                    try {
                        User userCourant = Session.getSession().getLeVisiteur();

                        if (NetworkUtil.isOnline()) {
                            // Déconnexion en ligne
                            String requestBody = "{\"token\":\"" + userCourant.getToken() + "\"}";
                            HttpResponseWrapper responseWrapper = httpService.sendDeleteRequest("auth/logout/" + userCourant.getId(), requestBody);
                            jsonResponse = responseWrapper.getBody();
                            statusCode = responseWrapper.getStatusCode();

                            if (statusCode == 201) {
                                System.out.println("Déconnexion réussie du serveur.");
                            } else {
                                System.out.println("Échec de la déconnexion du serveur.");
                            }
                        } else {
                            System.out.println("Déconnexion hors ligne.");
                        }

                        // Fermer la session localement dans les deux cas (en ligne et hors ligne)
                        Session.fermer();
                        Platform.exit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                alertQuitter.close();
            }
        });

    }

    private CalculatorPlugin loadCalculatorPlugin() {
        try {
            // Chemin vers le fichier JAR du plugin
            File pluginJar = new File("plugins/calculator-plugin-impl.jar");
            URL[] urls = {pluginJar.toURI().toURL()};
            URLClassLoader urlClassLoader = new URLClassLoader(urls);

            // Charger la classe du plugin
            Class<?> clazz = urlClassLoader.loadClass("com.ecaf.ecafclientjava.plugins.CalculatorPluginImpl");
            return (CalculatorPlugin) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    private NotePlugin loadNotePlugin() {
        try {
            // Chemin vers le fichier JAR du plugin
            File pluginJar = new File("plugins/note-plugin-impl.jar");
            URL[] urls = {pluginJar.toURI().toURL()};
            URLClassLoader urlClassLoader = new URLClassLoader(urls);

            // Charger la classe du plugin
            Class<?> clazz = urlClassLoader.loadClass("com.ecaf.ecafclientjava.plugins.NotePluginImpl");
            return (NotePlugin) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private KanbanPlugin loadKanbanPlugin() {
        try {
            // Chemin vers le fichier JAR du plugin
            File pluginJar = new File("plugins/kanban-plugin-impl.jar");
            URL[] urls = {pluginJar.toURI().toURL()};
            URLClassLoader urlClassLoader = new URLClassLoader(urls);

            // Charger la classe du plugin
            Class<?> clazz = urlClassLoader.loadClass("com.ecaf.ecafclientjava.plugins.KanbanPluginImpl");
            return (KanbanPlugin) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void checkForUpdates() {
        // Créer une nouvelle fenêtre pour afficher le loader
        Stage loaderStage = new Stage();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        VBox vbox = new VBox(progressIndicator);
        vbox.setStyle("-fx-padding: 20;");
        Scene loaderScene = new Scene(vbox);
        loaderStage.setScene(loaderScene);
        loaderStage.initModality(Modality.APPLICATION_MODAL);
        loaderStage.initStyle(StageStyle.UNDECORATED);
        loaderStage.setTitle("Mise à jour en cours...");
        loaderStage.setResizable(false);

        // Exécuter la mise à jour dans un nouveau thread
        new Thread(() -> {
            Platform.runLater(loaderStage::show); // Afficher le loader

            try {
                // Vérification de mise à jour
                if (UpdateManager.isUpdateAvailable()) {
                    System.out.println("Mise à jour disponible.");
                    UpdateManager.downloadUpdate();
                    System.out.println("Mise à jour téléchargée.");
                    UpdateManager.loadAndRun("update.jar", "com.ecaf.ecafclientjava.Main", "main", String[].class);
                } else {
                    System.out.println("Aucune mise à jour disponible.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Affiche un message d'erreur à l'utilisateur
                showAlert(AlertType.ERROR, "Échec de la vérification de la mise à jour.", "Voir les logs pour plus de détails");
            } finally {
                Platform.runLater(loaderStage::close); // Fermer le loader
            }
        }).start();
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
