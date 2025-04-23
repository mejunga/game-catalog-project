package com.example.gamecatalog.util;

import java.io.File;
import java.net.URL;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Utility class for managing help documentation.
 * Provides methods to show the help documentation in a new window.
 */
public class HelpManager {
    
    private static final String HELP_RESOURCE_PATH = "/help/user-guide.html";
    private static final int HELP_WINDOW_WIDTH = 950;
    private static final int HELP_WINDOW_HEIGHT = 700;
    
    /**
     * Opens the help documentation in a new window.
     */
    public static void showHelpDocument() {
        try {
            // Create a new stage for the help window
            Stage helpStage = new Stage();
            helpStage.setTitle("GameMage - Help");
            helpStage.initStyle(StageStyle.DECORATED);
            helpStage.initModality(Modality.APPLICATION_MODAL);
            helpStage.setWidth(HELP_WINDOW_WIDTH);
            helpStage.setHeight(HELP_WINDOW_HEIGHT);
            
            // Create a WebView to display the HTML content
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();
            
            // Load the help document from resources
            URL helpUrl = HelpManager.class.getResource(HELP_RESOURCE_PATH);
            if (helpUrl != null) {
                webEngine.load(helpUrl.toExternalForm());
            } else {
                System.err.println("Help resource not found: " + HELP_RESOURCE_PATH);
                return;
            }
            
            // Create layout and scene
            BorderPane root = new BorderPane();
            root.setCenter(webView);
            Scene scene = new Scene(root);
            
            helpStage.setScene(scene);
            helpStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error showing help document: " + e.getMessage());
        }
    }
} 