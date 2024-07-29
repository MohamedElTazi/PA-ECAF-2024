module com.ecaf.ecafclientjava {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires java.net.http;
    requires org.json;
    requires java.sql;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;
    requires com.calendarfx.view;
    requires java.desktop;
    requires okhttp3;
    requires spring.security.crypto;

    exports com.ecaf.ecafclientjava.entites;
    opens com.ecaf.ecafclientjava.entites to com.fasterxml.jackson.databind, com.google.gson;

    exports com.ecaf.ecafclientjava.technique;
    opens com.ecaf.ecafclientjava.technique to com.fasterxml.jackson.databind, com.google.gson;

    exports com.ecaf.ecafclientjava;
    exports com.ecaf.ecafclientjava.vue;

    // Exportez le package contenant ThemePlugin
    exports com.ecaf.ecafclientjava.plugins.theme;
}
