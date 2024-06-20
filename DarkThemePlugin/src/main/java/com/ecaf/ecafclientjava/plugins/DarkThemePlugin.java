package com.ecaf.ecafclientjava.plugins;
import com.ecaf.ecafclientjava.technique.Theme;
import com.ecaf.ecafclientjava.plugins.theme.ThemePlugin;
import javafx.scene.Scene;

public class DarkThemePlugin implements ThemePlugin {

    @Override
    public void applyTheme(Scene scene) {
        Theme.backgroudColorMain = "#333333";
        Theme.themeMain = "/com/ecaf/ecafclientjava/css/theme-sombre/main.css";
        Theme.themeTableauFormulaire = "/com/ecaf/ecafclientjava/css/theme-sombre/tableauFormulaire.css";
        Theme.themeAlert = "/com/ecaf/ecafclientjava/css/theme-sombre/alert.css";
        Theme.themeVueConnexion = "/com/ecaf/ecafclientjava/css/theme-sombre/vueConnexion.css";
        Theme.themeVueConnexionEchoueVide = "/com/ecaf/ecafclientjava/css/theme-sombre/vueConnexionEchoueVide.css";
        updateSceneStyles(scene);
        updateVisibleElements(scene);
    }

    @Override
    public String getThemeName() {
        return "sombre";
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