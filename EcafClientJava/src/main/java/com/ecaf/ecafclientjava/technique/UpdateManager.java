package com.ecaf.ecafclientjava.technique;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UpdateManager {
    private static final Logger LOGGER = Logger.getLogger(UpdateManager.class.getName());
    private static final String UPDATE_FILE_PATH = "update.jar";
    private static final String CURRENT_VERSION = "version_1.0.1";
    private static final String UPDATE_URL = "https://github.com/R-Mehdi94/ECAF-JAR/raw/main/ecafclientjava.jar";

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
        URL jarUrl = new URL("file:" + jarPath);
        URL[] urls = { jarUrl };

        // Utilisez un URLClassLoader isolé pour forcer le rechargement des classes
        try (URLClassLoader loader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader().getParent())) {
            LOGGER.log(Level.INFO, "Chargement du JAR depuis : " + jarPath);

            for (URL url : loader.getURLs()) {
                LOGGER.log(Level.INFO, "URL chargée : " + url);
            }

            Class<?> clazz = loader.loadClass(className);
            LOGGER.log(Level.INFO, "Classe chargée : " + clazz.getName() + " depuis " + clazz.getProtectionDomain().getCodeSource().getLocation());

            Method method = clazz.getMethod(methodName, parameterTypes);
            LOGGER.log(Level.INFO, "Méthode trouvée : " + method.getName());

            try {
                method.invoke(null, (Object) new String[] {});
                LOGGER.log(Level.INFO, "Méthode invoquée avec succès.");
            } catch (InvocationTargetException e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de l'invocation de la méthode (InvocationTargetException) : " + e.getTargetException().getMessage(), e);
                e.getTargetException().printStackTrace();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de l'invocation de la méthode : " + e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }
}