module com.example.gamecatalog {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.web;
    requires java.logging;

    opens com.example.gamecatalog.controller to javafx.fxml;
    exports com.example.gamecatalog;
    exports com.example.gamecatalog.model;
    exports com.example.gamecatalog.repository;
    exports com.example.gamecatalog.util;
}
