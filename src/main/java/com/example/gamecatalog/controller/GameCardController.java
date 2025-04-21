package com.example.gamecatalog.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import java.io.File;

public class GameCardController {
    @FXML private AnchorPane card_base;
    @FXML private ImageView game_image;
    @FXML private Label title;
    @FXML private Button game_options;
    @FXML private Label game_info;

    private Runnable onDoubleClickAction;

    public void setGameData(String title, String gameInfo){
        this.title.setText(title);
        game_info.setText(gameInfo);
        game_image.setImage(new Image(getClass().getResourceAsStream("/icons/blank-gamecard-icon.png")));
    }

    public void setGameData(String title, String gameInfo, String imagePath) {
        this.title.setText(title);
        game_info.setText(gameInfo);
        
        // Try to load image
        if (imagePath != null && !imagePath.isEmpty()) {
            // First try to load as a file on disk (for newly added images)
            File imageFile = new File("src/main/resources/" + imagePath);
            if (imageFile.exists()) {
                try {
                    String fileUrl = imageFile.toURI().toURL().toString();
                    Image image = new Image(fileUrl);
                    if (!image.isError()) {
                        game_image.setImage(image);
                        return;
                    }
                } catch (Exception e) {
                    System.err.println("Error loading image from file: " + e.getMessage());
                }
            }
            
            // Then try to load from resources (for images included in the app)
            try {
                Image image = new Image(getClass().getResourceAsStream("/" + imagePath));
                if (image != null && !image.isError()) {
                    game_image.setImage(image);
                    return;
                }
            } catch (Exception e) {
                System.err.println("Error loading image from resources: " + e.getMessage());
            }
        }
        
        // If all else fails, use default image
        try {
            game_image.setImage(new Image(getClass().getResourceAsStream("/icons/blank-gamecard-icon.png")));
        } catch (Exception e) {
            System.err.println("Error loading default image: " + e.getMessage());
        }
    }

    public void setOnDoubleClick(Runnable action) {
        this.onDoubleClickAction = action;
    }

    private void openGameDetails(){

    }

    @FXML public void handleCardClicked(MouseEvent e){
        if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2){
            if (!game_options.isHover()){
                if (onDoubleClickAction != null) onDoubleClickAction.run();
            }
        }
    }

    public void initialize(){

    }
}
