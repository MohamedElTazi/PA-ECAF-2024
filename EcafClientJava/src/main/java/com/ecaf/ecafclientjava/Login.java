package com.ecaf.ecafclientjava;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Login extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");

        TextField identifiant = new TextField();
        identifiant.setPromptText("Identifiant");

        PasswordField mdp = new PasswordField();
        mdp.setPromptText("Mot de passe");

        Button loginButton = new Button("Se connecter");
        Button cancelButton = new Button("Annuler");

        loginButton.setOnAction(e -> {
            String id = identifiant.getText();
            String password = mdp.getText();
            try {
                sendLoginRequest(id, password);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        cancelButton.setOnAction(e -> {
            identifiant.clear();
            mdp.clear();
        });

        VBox vbox = new VBox(10, new Label("Identifiant:"), identifiant, new Label("Mot de passe:"), mdp, loginButton, cancelButton);
        Scene scene = new Scene(vbox, 300, 200);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sendLoginRequest(String id, String password) throws Exception {
        URL url = new URL("http://yourserver.com/api/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = String.format("{\"id\": \"%s\", \"password\": \"%s\"}", id, password);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Login successful");
            // Handle successful login
        } else {
            System.out.println("Login failed");
            // Handle failed login
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
