package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.technique.Theme;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class VueConnexionVide extends Dialog {
    public String auth ="Authentification";
    public String saisie ="Identifiant ou mot de passe manquant";
    public String fermer = "Fermer";


    public VueConnexionVide(){
        this.setTitle(auth);
        this.setHeaderText(saisie);

        VBox vbSaisies = new VBox();
        vbSaisies.getStyleClass().add("vbox");

        Label label = new Label("Votre identifiant ou votre mot de passe est manquant");
        label.getStyleClass().add("label");

        vbSaisies.getChildren().add(label);


        this.getDialogPane().getStylesheets().add(getClass().getResource(Theme.themeVueConnexionEchoueVide).toExternalForm());
        this.getDialogPane().getStyleClass().add("dialog-pane");

        this.getDialogPane().setContent(vbSaisies);
        this.getDialogPane().setContent(vbSaisies);

        ButtonType FERMER = new ButtonType(fermer, ButtonBar.ButtonData.CANCEL_CLOSE);

        this.getDialogPane().getButtonTypes().add(FERMER);


    }
}
