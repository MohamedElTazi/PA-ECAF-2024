package com.ecaf.ecafclientjava.plugins;

import com.ecaf.ecafclientjava.plugins.theme.ThemePlugin;
import com.ecaf.ecafclientjava.technique.Theme;
import javafx.scene.Scene;

public class NatureThemePlugin implements ThemePlugin {

    @Override
    public void applyTheme(Scene scene) {
        Theme.backgroudColorMain = "#f0f0f0";
        Theme.themeMain = "/com/ecaf/ecafclientjava/css/theme-nature/main.css";
        Theme.themeTableauFormulaire = "/com/ecaf/ecafclientjava/css/theme-nature/tableauFormulaire.css";
        Theme.themeAlert = "/com/ecaf/ecafclientjava/css/theme-nature/alert.css";
        Theme.themeVueConnexion = "/com/ecaf/ecafclientjava/css/theme-nature/vueConnexion.css";
        Theme.themeVueConnexionEchoueVide = "/com/ecaf/ecafclientjava/css/theme-nature/vueConnexionEchoueVide.css";
        updateSceneStyles(scene);
        updateVisibleElements(scene);
    }

    @Override
    public String getThemeName() {
        return "nature";
    }

    private void updateSceneStyles(Scene scene) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource(Theme.themeMain).toExternalForm());
        scene.getStylesheets().add(getClass().getResource(Theme.themeTableauFormulaire).toExternalForm());
        scene.getStylesheets().add(getClass().getResource(Theme.themeAlert).toExternalForm());
        scene.getStylesheets().add(getClass().getResource(Theme.themeVueConnexion).toExternalForm());
        scene.getStylesheets().add(getClass().getResource(Theme.themeVueConnexionEchoueVide).toExternalForm());
    }

    private void updateVisibleElements(Scene scene) {
        scene.getRoot().applyCss();
        scene.getRoot().layout();
    }
}