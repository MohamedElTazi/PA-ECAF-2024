package com.ecaf.ecafclientjava.vue;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.util.Optional;

public class VueCalendrier extends Dialog<LocalDate> {

    public VueCalendrier() {
        this.setTitle("Sélection de Date");
        this.setHeaderText("Veuillez choisir une date");

        DatePicker datePicker = new DatePicker();

        VBox vbox = new VBox(new Label("Date :"), datePicker);
        this.getDialogPane().setContent(vbox);

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return datePicker.getValue();
            }
            return null;
        });
    }

    public static void main(String[] args) {
        VueCalendrier vueCalendrier = new VueCalendrier();
        Optional<LocalDate> date = vueCalendrier.showAndWait();
        date.ifPresent(selectedDate -> System.out.println("Selected Date: " + selectedDate));
    }
}
