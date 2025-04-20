package com.example.gamecatalog.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

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
    }

    public void setGameData(String title, String gameInfo, String imageURL){
        this.title.setText(title);
        game_info.setText(gameInfo);
        game_image.setImage(new Image(getClass().getResourceAsStream(imageURL)));
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
