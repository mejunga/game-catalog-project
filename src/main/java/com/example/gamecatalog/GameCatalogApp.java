package com.example.gamecatalog;

import com.example.gamecatalog.controller.GameCatalogController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GameCatalogApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        // Ensure required directories exist
        ensureDirectoriesExist();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game-catalog-view.fxml"));
        Parent root = loader.load();
        GameCatalogController controller = loader.getController();
        controller.setStage(primaryStage);

    
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setMinHeight(660);
        primaryStage.setMinWidth(1140);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Ensures that all required directories exist.
     */
    private void ensureDirectoriesExist() {
        try {
            // Ensure data directory exists
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            
            // Ensure JSON file exists
            Path jsonFile = Paths.get("data/games_all.json");
            if (!Files.exists(jsonFile)) {
                // Create empty JSON array if file doesn't exist
                Files.write(jsonFile, "[]".getBytes());
            }
            
            // Ensure images directory exists
            Path imagesDir = Paths.get("src/main/resources/images");
            if (!Files.exists(imagesDir)) {
                Files.createDirectories(imagesDir);
            }
        } catch (IOException e) {
            System.err.println("Failed to create required directories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}
