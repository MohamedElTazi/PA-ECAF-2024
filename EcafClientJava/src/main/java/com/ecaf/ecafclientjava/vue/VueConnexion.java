package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.technique.Theme;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;

public class VueConnexion extends Dialog<Pair<String, String>> {

    public String auth = "Authentification";
    public String saisie = "Saisir vos données de connexion";
    public String co = "Se connecter";
    public String an = "Annuler";

    public VueConnexion() {
        this.setTitle(auth);
        this.setHeaderText(saisie);

        TextField matricule = new TextField();
        TextField mdp = new TextField();

        VBox vbSaisies = new VBox();
        vbSaisies.getChildren().add(new Label("Matricule :"));
        vbSaisies.getChildren().add(matricule);

        vbSaisies.getChildren().add(new Label("Mot de passe:"));
        vbSaisies.getChildren().add(mdp);

        // Appliquer la classe CSS à la boîte de dialogue
        this.getDialogPane().getStylesheets().add(getClass().getResource(Theme.themeVueConnexionEchoueVide).toExternalForm());
        this.getDialogPane().getStyleClass().add("dialog-pane");

        this.getDialogPane().setContent(vbSaisies);

        ButtonType OK = new ButtonType(co, ButtonBar.ButtonData.OK_DONE);
        ButtonType CANCEL = new ButtonType(an, ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getDialogPane().getButtonTypes().addAll(OK, CANCEL);

        setResultConverter(new Callback<ButtonType, Pair<String, String>>() {
            @Override
            public Pair<String, String> call(ButtonType typeButton) {
                if (typeButton == OK) {
                    return new Pair<String, String>(matricule.getText(), mdp.getText());
                } else if (typeButton == CANCEL) {
                    // Do nothing
                }
                return null;
            }
        });
    }

}