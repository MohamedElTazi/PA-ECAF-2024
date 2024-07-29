package com.ecaf.ecafclientjava.technique;

import com.ecaf.ecafclientjava.entites.User;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateManager {
    private static final Logger LOGGER = Logger.getLogger(UpdateManager.class.getName());
    private static final String UPDATE_FILE_PATH = "update.jar";
    private static final String CURRENT_VERSION = "version_1.0.0";
    private static final String UPDATE_URL = "https://github.com/R-Mehdi94/ECAF-JAR/raw/main/ecafclientjava.jar";
    private static JsonNode jsonResponse;
    private static int statusCode;
    private static final HttpService httpService = new HttpService();

    public static boolean isUpdateAvailable() throws Exception {
        HttpService httpService = new HttpService();
        HttpResponseWrapper httpResponseWrapper = httpService.sendGetRequest("fileVersion");
        JsonNode jsonResponse = httpResponseWrapper.getBody();
        JsonNode fileVersionNode = jsonResponse.get("fileVersion");
        String fileVersion = fileVersionNode.asText();

        return !CURRENT_VERSION.equals(fileVersion);
    }

    public static void downloadUpdate() throws Exception {
        URL url = new URL(UPDATE_URL);
        try (InputStream in = new BufferedInputStream(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(UPDATE_FILE_PATH)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }

    public static void loadAndRun(String jarPath, String className, String methodName, Class<?>... parameterTypes) throws Exception {
        LOGGER.log(Level.INFO, "Chargement du JAR depuis : " + jarPath);

        // Chemin vers les bibliothèques JavaFX
        String javafxLibPath = "/home/r-mehdi/Téléchargements/openjfx-21.0.2_linux-x64_bin-sdk/javafx-sdk-21.0.2/lib";
        String javafxModules = String.join(File.pathSeparator,
                javafxLibPath + "/javafx.base.jar",
                javafxLibPath + "/javafx.controls.jar",
                javafxLibPath + "/javafx.fxml.jar",
                javafxLibPath + "/javafx.graphics.jar",
                javafxLibPath + "/javafx.media.jar",
                javafxLibPath + "/javafx.swing.jar",
                javafxLibPath + "/javafx.web.jar");

        // Créer le nouveau processus Java
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java", "--module-path", javafxLibPath, "--add-modules", "javafx.controls,javafx.fxml", "-cp", jarPath + File.pathSeparator + javafxModules, className
        );
        processBuilder.inheritIO(); // Pour voir la sortie du nouveau processus dans la console actuelle
        processBuilder.start();
        if (Session.getSession() == null) {
            System.exit(0);
        }
        try {
            User userCourant = Session.getSession().getLeVisiteur();
            String requestBody = "{\"token\":\"" + userCourant.getToken() + "\"}";
            HttpResponseWrapper responseWrapper = httpService.sendDeleteRequest("auth/logout/" + userCourant.getId(), requestBody);
            jsonResponse = responseWrapper.getBody();
            statusCode = responseWrapper.getStatusCode();

            if (statusCode == 201) {
                Session.fermer();
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);

    }

}
