package com.ecaf.ecafclientjava;

import com.ecaf.ecafclientjava.technique.UpdateManager;

public class Launcher {
    public static void main(String[] args) {
        /*try {
            // Vérification de mise à jour
            if (UpdateManager.isUpdateAvailable()) {
                System.out.println("Mise à jour disponible.");
                UpdateManager.downloadUpdate();
                System.out.println("Mise à jour téléchargée.");
                UpdateManager.loadAndRun("update.jar", "com.ecaf.ecafclientjava.Main", "main", String[].class);
                return;
            } else {
                System.out.println("Aucune mise à jour disponible.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        Main.main(args);
    }
}