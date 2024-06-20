package com.ecaf.ecafclientjava.plugins;

import com.ecaf.ecafclientjava.plugins.theme.ThemePlugin;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class PluginManager {
    private List<ThemePlugin> themePlugins;

    public PluginManager(String pluginsDir) throws Exception {
        themePlugins = new ArrayList<>();
        loadPlugins(pluginsDir);
    }

    private void loadPlugins(String pluginsDir) throws Exception {
        File dir = new File(pluginsDir);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Invalid plugins directory: " + pluginsDir);
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".jar"));
        if (files == null || files.length == 0) {
            throw new FileNotFoundException("No JAR files found in plugins directory: " + pluginsDir);
        }

        for (File file : files) {
            System.out.println("Loading JAR: " + file.getAbsolutePath());
            URL[] urls = { file.toURI().toURL() };
            try (URLClassLoader loader = new URLClassLoader(urls, getClass().getClassLoader())) {
                for (String className : getPluginClassNames(loader)) {
                    try {
                        System.out.println("Loading class: " + className);
                        Class<?> clazz = loader.loadClass(className);
                        if (ThemePlugin.class.isAssignableFrom(clazz)) {
                            ThemePlugin plugin = (ThemePlugin) clazz.getDeclaredConstructor().newInstance();
                            themePlugins.add(plugin);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private List<String> getPluginClassNames(URLClassLoader loader) {
        List<String> pluginClassNames = new ArrayList<>();
        pluginClassNames.add("com.ecaf.ecafclientjava.plugins.DarkThemePlugin");
        return pluginClassNames;
    }

    public List<ThemePlugin> getThemePlugins() {
        return themePlugins;
    }
}