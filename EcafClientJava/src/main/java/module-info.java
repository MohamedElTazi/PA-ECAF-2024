module com.ecaf.ecafclientjava {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires java.net.http; // Pour les requêtes HTTP si vous utilisez la classe java.net.http.HttpClient
    requires org.json;
    requires java.sql;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind; // Si vous utilisez la bibliothèque org.json pour la manipulation des JSON

    exports com.ecaf.ecafclientjava;
    exports com.ecaf.ecafclientjava.vue;
}
