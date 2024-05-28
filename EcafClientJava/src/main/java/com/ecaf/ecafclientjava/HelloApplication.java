package com.ecaf.ecafclientjava;

import com.ecaf.ecafclientjava.technique.ConnexionBDD;
import com.ecaf.ecafclientjava.technique.ConnexionException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, ConnexionException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        Connection connexionBdd = ConnexionBDD.getConnexion();

    }

    public static void main(String[] args) {
        launch();
    }
}