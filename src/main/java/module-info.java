module com.example.gamecatalog {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.example.gamecatalog.controller to javafx.fxml;
    exports com.example.gamecatalog;
}
