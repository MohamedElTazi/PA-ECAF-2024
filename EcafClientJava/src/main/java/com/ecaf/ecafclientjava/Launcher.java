package com.ecaf.ecafclientjava;

import com.ecaf.ecafclientjava.technique.UpdateManager;

public class Launcher {
    public static void main(String[] args) {
        try {
            // Vérification de mise à jour
            if (UpdateManager.isUpdateAvailable()) {
                System.out.println("Mise à jour disponible.");
                UpdateManager.downloadUpdate();
                System.out.println("Mise à jour téléchargée.");
                UpdateManager.loadAndRun("update.jar", "com.ecaf.ecafclientjava.Main", "main", String[].class);
                return; // Ne pas lancer l'application actuelle, car la nouvelle version sera chargée dynamiquement
            } else {
                System.out.println("Aucune mise à jour disponible.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Lancer l'application normalement si aucune mise à jour n'est disponible
        Main.main(args);
    }
}