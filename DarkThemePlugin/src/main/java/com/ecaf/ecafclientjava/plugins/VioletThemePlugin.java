package com.ecaf.ecafclientjava.plugins;

import com.ecaf.ecafclientjava.plugins.theme.ThemePlugin;
import com.ecaf.ecafclientjava.technique.Theme;
import javafx.scene.Scene;

public class VioletThemePlugin implements ThemePlugin {

    @Override
    public void applyTheme(Scene scene) {
        Theme.backgroudColorMain = "#333333";
        Theme.themeMain = "/com/ecaf/ecafclientjava/css/theme-violet/main.css";
        Theme.themeTableauFormulaire = "/com/ecaf/ecafclientjava/css/theme-violet/tableauFormulaire.css";
        Theme.themeAlert = "/com/ecaf/ecafclientjava/css/theme-violet/alert.css";
        Theme.themeVueConnexion = "/com/ecaf/ecafclientjava/css/theme-violet/vueConnexion.css";
        Theme.themeVueConnexionEchoueVide = "/com/ecaf/ecafclientjava/css/theme-violet/vueConnexionEchoueVide.css";
        updateSceneStyles(scene);
        updateVisibleElements(scene);
    }

    @Override
    public String getThemeName() {
        return "violet";
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