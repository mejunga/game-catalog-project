package com.example.gamecatalog;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GameCatalogController 
{
    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;
    private final int RESIZE_MARGIN = 7;
    private boolean isMaximized = false;
    private double prevX, prevY, prevWidth, prevHeight;

    @FXML
    private AnchorPane base;

    @FXML
    private AnchorPane title_bar;
    @FXML
    private Button minimize;
    @FXML
    private Button restore_maximize;
    @FXML
    private Button close;
    @FXML
    private MenuButton file_menu;
    @FXML 
    private Button help;
    
    @FXML
    private Button show_all;
    @FXML 
    private Button show_favorites;
    @FXML
    private MenuButton sort_menu;
    @FXML
    private FlowPane game_card_flow;
    @FXML
    private Button first_page;
    @FXML
    private Button previous_page;
    @FXML
    private TextField page_number;
    @FXML
    private Button next_page;
    @FXML
    private Button last_page;

    @FXML
    private TextField search_field;
    @FXML 
    private MenuButton genre_filter;
    @FXML 
    private MenuButton platform_filter;
    @FXML 
    private MenuButton tags_filter;
    @FXML 
    private MenuButton release_year_filter;
    @FXML 
    private MenuButton publisher_filter;
    @FXML 
    private MenuButton developper_filter;
    @FXML
    private Button add_game;

    private void handleClose(){
        Platform.exit();
    }

    private void handleRestoreMaximize(){
        if(stage == null){
            return;
        }

        if(isMaximized){
            stage.setX(prevX);
            stage.setY(prevY);
            stage.setWidth(prevWidth);
            stage.setHeight(prevHeight);
            isMaximized = false;
        }else{
            prevX = stage.getX();
            prevY = stage.getY();
            prevWidth = stage.getWidth();
            prevHeight = stage.getHeight();

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            isMaximized = true;
        }
    }

    private void handleMinimize(){
        if(stage == null){
            return;
        }
        stage.setIconified(true);
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    private Cursor getCursorForPosition(MouseEvent e){
        double mouseX = e.getScreenX();
        double mouseY = e.getScreenY();

        Point2D topLeft = base.localToScreen(0, 0);
        Point2D bottomRight = base.localToScreen(base.getWidth(), base.getHeight());
    
        double leftX = topLeft.getX();
        double rightX = bottomRight.getX();

        double topY = topLeft.getY();
        double bottomY = bottomRight.getY();
    
        if (Math.abs(mouseX - leftX) <= RESIZE_MARGIN) {
            if (Math.abs(mouseY - topY) <= RESIZE_MARGIN) {
                return Cursor.NW_RESIZE;
            } else if (Math.abs(mouseY - bottomY) <= RESIZE_MARGIN) {
                return Cursor.SW_RESIZE;
            }
            return Cursor.W_RESIZE;
        }
    
        if (Math.abs(mouseX - rightX) <= RESIZE_MARGIN) {
            if (Math.abs(mouseY - topY) <= RESIZE_MARGIN) {
                return Cursor.NE_RESIZE;
            } else if (Math.abs(mouseY - bottomY) <= RESIZE_MARGIN) {
                return Cursor.SE_RESIZE;
            }
            return Cursor.E_RESIZE;
        }
    
        if (Math.abs(mouseY - topY) <= RESIZE_MARGIN) {
            return Cursor.N_RESIZE;
        }
    
        if (Math.abs(mouseY - bottomY) <= RESIZE_MARGIN) {
            return Cursor.S_RESIZE;
        }
    
        return Cursor.DEFAULT;
    }

    private void resizeWindow(MouseEvent e) {
        if (stage == null) return;
    
        double mouseX = e.getScreenX();
        double mouseY = e.getScreenY();
        double stageX = stage.getX();
        double stageY = stage.getY();
        double stageWidth = stage.getWidth();
        double stageHeight = stage.getHeight();
    
        Cursor cursor = base.getCursor();
    
        if (cursor == Cursor.NW_RESIZE) {
            double newWidth = stageWidth - (mouseX - stageX);
            double newHeight = stageHeight - (mouseY - stageY);
            if (newWidth > 1110) {
                stage.setX(mouseX);
                stage.setWidth(newWidth);
            }
            if (newHeight > 660) {
                stage.setY(mouseY);
                stage.setHeight(newHeight);
            }
        } else if (cursor == Cursor.N_RESIZE) {
            double newHeight = stageHeight - (mouseY - stageY);
            if (newHeight > 660) {
                stage.setY(mouseY);
                stage.setHeight(newHeight);
            }
        } else if (cursor == Cursor.W_RESIZE) {
            double newWidth = stageWidth - (mouseX - stageX);
            if (newWidth > 1110) {
                stage.setX(mouseX);
                stage.setWidth(newWidth);
            }
        } else if (cursor == Cursor.SW_RESIZE) {
            double newWidth = stageWidth - (mouseX - stageX);
            double newHeight = mouseY - stageY;
            if (newWidth > 1110) {
                stage.setX(mouseX);
                stage.setWidth(newWidth);
            }
            stage.setHeight(newHeight);
        } else if (cursor == Cursor.NE_RESIZE) {
            double newWidth = mouseX - stageX;
            double newHeight = stageHeight - (mouseY - stageY);
            if (newHeight > 660) {
                stage.setY(mouseY);
                stage.setHeight(newHeight);
            }
            stage.setWidth(newWidth);
        } else if (cursor == Cursor.SE_RESIZE) {
            stage.setWidth(mouseX - stageX);
            stage.setHeight(mouseY - stageY);
        } else if (cursor == Cursor.E_RESIZE) {
            stage.setWidth(mouseX - stageX);
        } else if (cursor == Cursor.S_RESIZE) {
            stage.setHeight(mouseY - stageY);
        }
    }
    
    public void initialize() {
        Platform.runLater(() -> {
            base.setOnMouseMoved(e -> {
                base.setCursor(getCursorForPosition(e));
            });
    
            base.setOnMousePressed(e -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });
    
            base.setOnMouseDragged(e -> {
                resizeWindow(e);
            });
        });

        minimize.setOnAction(e -> handleMinimize());
        restore_maximize.setOnAction(e -> handleRestoreMaximize());
        close.setOnAction(e -> handleClose());
    }
}
