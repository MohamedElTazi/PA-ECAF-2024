package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.technique.Theme;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class VueConnexionEchoue extends Dialog<Void> {
    public String auth = "Authentification";
    public String saisie = "Identifiant ou mot de passe invalide";
    public String fermer = "Fermer";

    public VueConnexionEchoue() {
        this.setTitle(auth);
        this.setHeaderText(saisie);

        VBox vbSaisies = new VBox();
        vbSaisies.getStyleClass().add("vbox");

        Label label = new Label("L'identifiant ou le mot de passe que vous avez saisi est incorrect.");
        label.getStyleClass().add("label");

        vbSaisies.getChildren().add(label);

        // Appliquer la classe CSS à la boîte de dialogue
        this.getDialogPane().getStylesheets().add(getClass().getResource(Theme.themeVueConnexionEchoueVide).toExternalForm());
        this.getDialogPane().getStyleClass().add("dialog-pane");

        this.getDialogPane().setContent(vbSaisies);

        ButtonType FERMER = new ButtonType(fermer, ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getDialogPane().getButtonTypes().add(FERMER);
    }

}
