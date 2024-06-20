package com.ecaf.ecafclientjava.technique;

import javafx.scene.Scene;

public class Theme{
    public static String themeMain = "/com/ecaf/ecafclientjava/css/theme-clair/main.css";
    public static String themeTableauFormulaire = "/com/ecaf/ecafclientjava/css/theme-clair/tableauFormulaire.css";
    public static String themeAlert = "/com/ecaf/ecafclientjava/css/theme-clair/alert.css";
    public static String themeVueConnexion = "/com/ecaf/ecafclientjava/css/theme-clair/vueConnexion.css";
    public static String themeVueConnexionEchoueVide = "/com/ecaf/ecafclientjava/css/theme-clair/vueConnexionEchoueVide.css";
    public static String themeVueCalendrier = "/com/ecaf/ecafclientjava/css/theme-clair/vueCalendrier.css";
    public static String backgroudColorMain = "#f0f0f0";


    public static void applyTheme(String themeType, Scene scene) {
        if (themeType.equalsIgnoreCase("clair")) {
            setClairTheme(scene);
        } else {
            throw new IllegalArgumentException("Theme type not recognized: " + themeType);
        }
    }

    private static void setClairTheme(Scene scene) {
        Theme.backgroudColorMain = "#f0f0f0";
        Theme.themeMain = "/com/ecaf/ecafclientjava/css/theme-clair/main.css";
        Theme.themeTableauFormulaire = "/com/ecaf/ecafclientjava/css/theme-clair/tableauFormulaire.css";
        Theme.themeAlert = "/com/ecaf/ecafclientjava/css/theme-clair/alert.css";
        Theme.themeVueConnexion = "/com/ecaf/ecafclientjava/css/theme-clair/vueConnexion.css";
        Theme.themeVueConnexionEchoueVide = "/com/ecaf/ecafclientjava/css/theme-clair/vueConnexionEchoueVide.css";
        updateSceneStyles(scene);
        updateVisibleElements(scene);
    }



    private static void updateSceneStyles(Scene scene) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Theme.themeMain);
        scene.getStylesheets().add(Theme.themeTableauFormulaire);
        scene.getStylesheets().add(Theme.themeAlert);
        scene.getStylesheets().add(Theme.themeVueConnexion);
        scene.getStylesheets().add(Theme.themeVueConnexionEchoueVide);
    }

    private static void updateVisibleElements(Scene scene) {
        scene.getRoot().applyCss();
        scene.getRoot().layout();
    }
}