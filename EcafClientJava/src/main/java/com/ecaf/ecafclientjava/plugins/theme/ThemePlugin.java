package com.ecaf.ecafclientjava.plugins.theme;

import javafx.scene.Scene;

public interface ThemePlugin {
    void applyTheme(Scene scene);
    String getThemeName();
}
