package com.ecaf.ecafclientjava.vue;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class VueConnexionEchoue extends Dialog {
    public String auth ="Authentification";
    public String saisie ="Identifiant ou mot de passe invalide";
    public String fermer = "Fermer";


    public VueConnexionEchoue(){
        this.setTitle(auth);
        this.setHeaderText(saisie);

        VBox vbSaisies = new VBox();

        Label label = new Label("L'identifiant ou le mot de passe que vous avez saisie est incorrecte");

        vbSaisies.getChildren().add(label);



        this.getDialogPane().setContent(vbSaisies);

        ButtonType FERMER = new ButtonType(fermer, ButtonBar.ButtonData.CANCEL_CLOSE);

        this.getDialogPane().getButtonTypes().add(FERMER);


    }

}
