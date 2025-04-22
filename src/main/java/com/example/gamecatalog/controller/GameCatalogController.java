package com.example.gamecatalog.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.gamecatalog.model.Game;
import com.example.gamecatalog.repository.GameRepository;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GameCatalogController
{
    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;
    private final int RESIZE_MARGIN = 7;
    private boolean isMaximized = false;
    private double prevX, prevY, prevWidth, prevHeight, dragOffsetX, dragOffsetY;
    private int pageNumber = 1;
    private static int maxPage = 1;
    private List<Game> allGamesList;
    private List<Game> filteredGamesList;
    private String currentGenreFilter = null;
    private String currentPlatformFilter = null;
    private String currentTagFilter = null;
    private String currentPublisherFilter = null;
    private String currentDeveloperFilter = null;
    private int[] currentYearRange = null;
    private String currentSort = null;
    private GameRepository gameRepository;

    @FXML private AnchorPane base;

    @FXML private AnchorPane title_bar;
    @FXML private Button minimize;
    @FXML private Button restore_maximize;
    @FXML private Button close;
    @FXML private MenuButton file_menu;
    @FXML private Button help;

    @FXML private Button show_all;
    @FXML private Button show_favorites;
    @FXML private MenuButton sort_menu;
    @FXML private FlowPane game_card_flow;
    @FXML private Button first_page;
    @FXML private Button previous_page;
    @FXML private TextField page_number;
    @FXML private Button next_page;
    @FXML private Button last_page;

    @FXML private TextField search_field;
    @FXML private MenuButton genre_filter;
    @FXML private MenuButton platform_filter;
    @FXML private MenuButton tags_filter;
    @FXML private MenuButton release_year_filter;
    @FXML private MenuButton publisher_filter;
    @FXML private MenuButton developper_filter;
    @FXML private Button add_game;

    @FXML private void handleClose(){
        Platform.exit();
    }

    @FXML private void handleRestoreMaximize(){
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
            stage.setX(screenBounds.getMinX()-5);
            stage.setY(screenBounds.getMinY()-5);
            stage.setWidth(screenBounds.getWidth()+10);
            stage.setHeight(screenBounds.getHeight()+10);
            isMaximized = true;
        }
    }

    @FXML private void handleMinimize(){
        if(stage == null){
            return;
        }
        stage.setIconified(true);
    }

    @FXML private void handleFirstPage(){

    }

    @FXML private void handleLastPage(){

    }

    @FXML private void handleNextPage(){

    }

    @FXML private void handlePreviousPage(){

    }

    @FXML private void handleAddGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add-game-form.fxml"));
            Parent root = loader.load();

            AddGameController controller = loader.getController();

            Stage addGameStage = new Stage();
            addGameStage.initModality(Modality.APPLICATION_MODAL);
            addGameStage.initStyle(StageStyle.UNDECORATED);
            addGameStage.setScene(new Scene(root));

            controller.setStage(addGameStage);

            addGameStage.showAndWait();

            // Force a full refresh of the game list
            forceRefreshGameList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Forces a complete refresh of the game list by reloading from repository
     * and rebuilding the UI components.
     */
    private void forceRefreshGameList() {
        // Clear existing items first
        Platform.runLater(() -> game_card_flow.getChildren().clear());

        // Create a new repository instance and reload games
        GameRepository gameRepository = new GameRepository();
        allGamesList = gameRepository.getAllGames();

        // Wait a moment to ensure file system operations complete
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Rebuild the flow pane
        new Thread(() -> {
            Platform.runLater(() -> {
                // Clear again just to be safe
                game_card_flow.getChildren().clear();

                // Render games
                render(allGamesList, game_card_flow, pageNumber);

                // Update page number display
                page_number.setText(String.valueOf(pageNumber));
            });
        }).start();
    }

    /**
     * Refreshes the game list by loading games from repository.
     */
    private void refreshGameList() {
        allGamesList = gameRepository.getAllGames();
        filteredGamesList = allGamesList; // Initialize filtered list with all games
        render(filteredGamesList, game_card_flow, pageNumber);
    }

    public static void render(List<Game> list, FlowPane flowPane, int page) {
        maxPage = (int) Math.ceil((double) list.size() / 100);

        // Clear the existing flow pane items
        Platform.runLater(() -> flowPane.getChildren().clear());

        new Renderer(list, flowPane, page).start();
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public FlowPane getFlowPane(){
        return game_card_flow;
    }

    public static int getMaxPage() {
        return maxPage;
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

        if(!isMaximized){
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

            if (Math.abs(mouseY - topY) <= RESIZE_MARGIN - 1) {
                return Cursor.N_RESIZE;
            }

            if (Math.abs(mouseY - bottomY) <= RESIZE_MARGIN) {
                return Cursor.S_RESIZE;
            }
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
        if(!isMaximized){
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
    }

    private static class Renderer extends Thread {
        private final List<Game> gameList;
        private final FlowPane flowPane;
        private final int pageNumber;
        private final GameRepository gameRepository;

        public Renderer(List<Game> list, FlowPane flowPane, int page) {
            this.gameList = list;
            this.flowPane = flowPane;
            this.pageNumber = page;
            this.gameRepository = new GameRepository();
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                int index = i + 100 * (pageNumber - 1);
                if (index >= gameList.size()) break;

                final int gameIndex = index; // Final copy for lambda expressions
                Game game = gameList.get(gameIndex);

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game-card-view.fxml"));
                    Node card = loader.load();

                    GameCardController controller = loader.getController();

                    // Pass the cover image path if available
                    if (game.getCoverImagePath() != null && !game.getCoverImagePath().isEmpty()) {
                        controller.setGameData(
                                game.getTitle(),
                                game.getPublisher() + " / " + game.getReleaseYear(),
                                game.getCoverImagePath()
                        );
                    } else {
                        controller.setGameData(
                                game.getTitle(),
                                game.getPublisher() + " / " + game.getReleaseYear()
                        );
                    }

                    // Set up the context menu actions
                    controller.setOnUpdateGame(() -> handleUpdateGame(gameIndex, game));
                    controller.setOnRemoveGame(() -> handleRemoveGame(gameIndex, game));
                    controller.setOnAddToFavorite(() -> handleAddToFavorite(game));

                    Platform.runLater(() -> flowPane.getChildren().add(card));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleUpdateGame(int index, Game game) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add-game-form.fxml"));
                Parent root = loader.load();

                AddGameController controller = loader.getController();
                controller.setForUpdate(game, index);

                Stage updateGameStage = new Stage();
                updateGameStage.initModality(Modality.APPLICATION_MODAL);
                updateGameStage.initStyle(StageStyle.UNDECORATED);
                updateGameStage.setScene(new Scene(root));

                controller.setStage(updateGameStage);

                updateGameStage.showAndWait();

                // Refresh game list after update
                Platform.runLater(() -> {
                    GameCatalogController mainController = getGameCatalogController();
                    if (mainController != null) {
                        mainController.forceRefreshGameList();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to open update form: " + e.getMessage());
            }
        }

        private void handleRemoveGame(int index, Game game) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Remove Game");
            alert.setHeaderText("Remove " + game.getTitle());
            alert.setContentText("Are you sure you want to remove this game from your catalog?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                // Create a new repository instance to ensure fresh data
                GameRepository gameRepository = new GameRepository();

                // Find the game by title and other properties since the index might be wrong
                List<Game> allGames = gameRepository.getAllGames();
                int correctIndex = -1;

                // Find the game by matching title, publisher and developer
                for (int i = 0; i < allGames.size(); i++) {
                    Game g = allGames.get(i);
                    if (g.getTitle().equals(game.getTitle()) &&
                            g.getPublisher().equals(game.getPublisher()) &&
                            g.getDeveloper().equals(game.getDeveloper())) {
                        correctIndex = i;
                        break;
                    }
                }

                if (correctIndex >= 0 && gameRepository.removeGame(correctIndex)) {
                    // Save changes to the JSON file
                    boolean saved = gameRepository.saveGames();
                    if (saved) {
                        showAlert("Success", "Game removed successfully");

                        // Refresh game list after removal
                        Platform.runLater(() -> {
                            GameCatalogController mainController = getGameCatalogController();
                            if (mainController != null) {
                                mainController.forceRefreshGameList();
                            }
                        });
                    } else {
                        showAlert("Error", "Failed to save changes after removing the game.");
                    }
                } else {
                    showAlert("Error", "Failed to find or remove the game from catalog.");
                }
            }
        }

        private void handleAddToFavorite(Game game) {
            // Set the favorite flag in the game object
            List<String> tags = game.getTags();
            if (!tags.contains("Favorite")) {
                tags.add("Favorite");
                game.setTags(tags);

                // Save the updated game
                gameRepository.saveGames();

                showAlert("Success", game.getTitle() + " added to favorites!");
            } else {
                showAlert("Info", game.getTitle() + " is already in favorites.");
            }
        }

        private void showAlert(String title, String message) {
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            });
        }

        private GameCatalogController getGameCatalogController() {
            // This is a simplified approach - in a real app you would use a proper
            // reference to the main controller via dependency injection or similar
            try {
                for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                    if (window instanceof Stage) {
                        Stage stage = (Stage) window;
                        if (stage.getScene() != null && stage.getScene().getRoot() != null) {
                            return (GameCatalogController) stage.getScene().getUserData();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void initialize(){
        // Initialize the repository
        gameRepository = new GameRepository();

        // Load games from repository
        refreshGameList();

        // Initialize the page number field
        page_number.setText(String.valueOf(pageNumber));

        // Store this controller in the scene's user data for access from other classes
        Platform.runLater(() -> {
            if (stage != null && stage.getScene() != null) {
                stage.getScene().setUserData(this);
            }

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

        title_bar.setOnMousePressed(e -> {
            Cursor cursor = title_bar.getCursor();
            if(cursor == null || cursor == Cursor.DEFAULT){
                dragOffsetX = e.getSceneX();
                dragOffsetY = e.getSceneY();
            }
        });

        title_bar.setOnMouseDragged(e -> {
            if(stage != null && !isMaximized) {
                stage.setX(e.getScreenX() - dragOffsetX);
                stage.setY(e.getScreenY() - dragOffsetY);
            }
        });

        // Setup filters
        setupFilters();
    }

    /**
     * Sets up the filter UI components with data from the repository
     */
    private void setupFilters() {
        // Setup genre filter
        setupGenreFilter();

        // Setup platform filter
        setupPlatformFilter();

        // Setup tags filter
        setupTagsFilter();

        // Setup publisher filter
        setupPublisherFilter();

        // Setup developer filter
        setupDeveloperFilter();

        // Setup release year filter
        setupReleaseYearFilter();

        // Setup sort menu
        setupSortMenu();
    }

    /**
     * Sets up the genre filter MenuButton with items from the repository
     */
    private void setupGenreFilter() {
        // Clear existing items
        genre_filter.getItems().clear();

        // Add "All Genres" option
        javafx.scene.control.MenuItem allGenresItem = new javafx.scene.control.MenuItem("All Genres");
        allGenresItem.setOnAction(e -> {
            genre_filter.setText("Genre");
            currentGenreFilter = null;
            applyFilters();
        });
        genre_filter.getItems().add(allGenresItem);

        // Get all unique genres from the repository
        List<String> genres = gameRepository.getAllGenres();

        // Add each genre as a menu item
        for (String genre : genres) {
            javafx.scene.control.MenuItem item = new javafx.scene.control.MenuItem(genre);
            item.setOnAction(e -> {
                genre_filter.setText(genre);
                currentGenreFilter = genre;
                applyFilters();
            });
            genre_filter.getItems().add(item);
        }
    }

    /**
     * Sets up the platform filter MenuButton with items from the repository
     */
    private void setupPlatformFilter() {
        // Clear existing items
        platform_filter.getItems().clear();

        // Add "All Platforms" option
        javafx.scene.control.MenuItem allPlatformsItem = new javafx.scene.control.MenuItem("All Platforms");
        allPlatformsItem.setOnAction(e -> {
            platform_filter.setText("Platform");
            currentPlatformFilter = null;
            applyFilters();
        });
        platform_filter.getItems().add(allPlatformsItem);

        // Get all unique platforms from the repository
        List<String> platforms = gameRepository.getAllPlatforms();

        // Add each platform as a menu item
        for (String platform : platforms) {
            javafx.scene.control.MenuItem item = new javafx.scene.control.MenuItem(platform);
            item.setOnAction(e -> {
                platform_filter.setText(platform);
                currentPlatformFilter = platform;
                applyFilters();
            });
            platform_filter.getItems().add(item);
        }
    }

    /**
     * Sets up the tags filter MenuButton with items from the repository
     */
    private void setupTagsFilter() {
        // Clear existing items
        tags_filter.getItems().clear();

        // Add "All Tags" option
        javafx.scene.control.MenuItem allTagsItem = new javafx.scene.control.MenuItem("All Tags");
        allTagsItem.setOnAction(e -> {
            tags_filter.setText("Tags");
            currentTagFilter = null;
            applyFilters();
        });
        tags_filter.getItems().add(allTagsItem);

        // Get all unique tags from the repository
        List<String> tags = gameRepository.getAllTags();

        // Add each tag as a menu item
        for (String tag : tags) {
            javafx.scene.control.MenuItem item = new javafx.scene.control.MenuItem(tag);
            item.setOnAction(e -> {
                tags_filter.setText(tag);
                currentTagFilter = tag;
                applyFilters();
            });
            tags_filter.getItems().add(item);
        }
    }

    /**
     * Sets up the release year filter MenuButton with year ranges
     */
    private void setupReleaseYearFilter() {
        // Clear existing items
        release_year_filter.getItems().clear();

        // Add "All Years" option
        javafx.scene.control.MenuItem allYearsItem = new javafx.scene.control.MenuItem("All Years");
        allYearsItem.setOnAction(e -> {
            release_year_filter.setText("Release Year");
            currentYearRange = null;
            applyFilters();
        });
        release_year_filter.getItems().add(allYearsItem);

        // Calculate year ranges in 5-year increments
        int maxYear = gameRepository.getMaxReleaseYear();
        int minYear = gameRepository.getMinReleaseYear();

        // Round max year up to next 5-year boundary
        maxYear = ((maxYear + 4) / 5) * 5;

        // Create ranges from max to min in 5-year increments
        for (int endYear = maxYear; endYear > minYear; endYear -= 5) {
            int startYear = endYear - 4;
            String rangeText = endYear + "-" + startYear;

            javafx.scene.control.MenuItem item = new javafx.scene.control.MenuItem(rangeText);
            final int start = startYear;
            final int end = endYear;

            item.setOnAction(e -> {
                release_year_filter.setText(rangeText);
                currentYearRange = new int[]{start, end};
                applyFilters();
            });

            release_year_filter.getItems().add(item);
        }
    }

    /**
     * Sets up the publisher filter MenuButton with items from the repository
     */
    private void setupPublisherFilter() {
        // Clear existing items
        publisher_filter.getItems().clear();

        // Add "All Publishers" option
        javafx.scene.control.MenuItem allPublishersItem = new javafx.scene.control.MenuItem("All Publishers");
        allPublishersItem.setOnAction(e -> {
            publisher_filter.setText("Publisher");
            currentPublisherFilter = null;
            applyFilters();
        });
        publisher_filter.getItems().add(allPublishersItem);

        // Get all unique publishers from the repository
        List<String> publishers = gameRepository.getAllPublishers();

        // Add each publisher as a menu item
        for (String publisher : publishers) {
            javafx.scene.control.MenuItem item = new javafx.scene.control.MenuItem(publisher);
            item.setOnAction(e -> {
                publisher_filter.setText(publisher);
                currentPublisherFilter = publisher;
                applyFilters();
            });
            publisher_filter.getItems().add(item);
        }
    }

    /**
     * Sets up the developer filter MenuButton with items from the repository
     */
    private void setupDeveloperFilter() {
        // Clear existing items
        developper_filter.getItems().clear();

        // Add "All Developers" option
        javafx.scene.control.MenuItem allDevelopersItem = new javafx.scene.control.MenuItem("All Developers");
        allDevelopersItem.setOnAction(e -> {
            developper_filter.setText("Developper");
            currentDeveloperFilter = null;
            applyFilters();
        });
        developper_filter.getItems().add(allDevelopersItem);

        // Get all unique developers from the repository
        List<String> developers = gameRepository.getAllDevelopers();

        // Add each developer as a menu item
        for (String developer : developers) {
            javafx.scene.control.MenuItem item = new javafx.scene.control.MenuItem(developer);
            item.setOnAction(e -> {
                developper_filter.setText(developer);
                currentDeveloperFilter = developer;
                applyFilters();
            });
            developper_filter.getItems().add(item);
        }
    }

    /**
     * Sets up the sort menu with title sorting options
     */
    private void setupSortMenu() {
        // Clear existing items
        sort_menu.getItems().clear();

        // Add default sort option
        javafx.scene.control.MenuItem defaultSortItem = new javafx.scene.control.MenuItem("Default");
        defaultSortItem.setOnAction(e -> {
            sort_menu.setText("Sort");
            currentSort = null;
            applyFilters();
        });
        sort_menu.getItems().add(defaultSortItem);

        // Add title A-Z sort option
        javafx.scene.control.MenuItem titleAscItem = new javafx.scene.control.MenuItem("Title A-Z");
        titleAscItem.setOnAction(e -> {
            sort_menu.setText("Title A-Z");
            currentSort = "title_asc";
            applyFilters();
        });
        sort_menu.getItems().add(titleAscItem);

        // Add title Z-A sort option
        javafx.scene.control.MenuItem titleDescItem = new javafx.scene.control.MenuItem("Title Z-A");
        titleDescItem.setOnAction(e -> {
            sort_menu.setText("Title Z-A");
            currentSort = "title_desc";
            applyFilters();
        });
        sort_menu.getItems().add(titleDescItem);

        // Add year (oldest to newest) sort option
        javafx.scene.control.MenuItem yearAscItem = new javafx.scene.control.MenuItem("Year (Oldest-Newest)");
        yearAscItem.setOnAction(e -> {
            sort_menu.setText("Year (Oldest-Newest)");
            currentSort = "year_asc";
            applyFilters();
        });
        sort_menu.getItems().add(yearAscItem);

        // Add year (newest to oldest) sort option
        javafx.scene.control.MenuItem yearDescItem = new javafx.scene.control.MenuItem("Year (Newest-Oldest)");
        yearDescItem.setOnAction(e -> {
            sort_menu.setText("Year (Newest-Oldest)");
            currentSort = "year_desc";
            applyFilters();
        });
        sort_menu.getItems().add(yearDescItem);
    }

    /**
     * Applies all active filters to the game list
     */
    private void applyFilters() {
        // Start with all games
        filteredGamesList = gameRepository.getAllGames();

        // Apply genre filter if active
        if (currentGenreFilter != null && !currentGenreFilter.isEmpty()) {
            filteredGamesList = gameRepository.getGamesByGenre(currentGenreFilter);
        }

        // Apply platform filter if active
        if (currentPlatformFilter != null && !currentPlatformFilter.isEmpty()) {
            // We need to further filter the already filtered list
            List<Game> platformFilteredGames = new ArrayList<>();
            for (Game game : filteredGamesList) {
                if (game.getPlatforms() != null && game.getPlatforms().contains(currentPlatformFilter)) {
                    platformFilteredGames.add(game);
                }
            }
            filteredGamesList = platformFilteredGames;
        }

        // Apply tag filter if active
        if (currentTagFilter != null && !currentTagFilter.isEmpty()) {
            // We need to further filter the already filtered list
            List<Game> tagFilteredGames = new ArrayList<>();
            for (Game game : filteredGamesList) {
                if (game.getTags() != null && game.getTags().contains(currentTagFilter)) {
                    tagFilteredGames.add(game);
                }
            }
            filteredGamesList = tagFilteredGames;
        }

        // Apply publisher filter if active
        if (currentPublisherFilter != null && !currentPublisherFilter.isEmpty()) {
            // We need to further filter the already filtered list
            List<Game> publisherFilteredGames = new ArrayList<>();
            for (Game game : filteredGamesList) {
                String publisher = game.getPublisher();
                if (publisher != null && publisher.equals(currentPublisherFilter)) {
                    publisherFilteredGames.add(game);
                }
            }
            filteredGamesList = publisherFilteredGames;
        }

        // Apply developer filter if active
        if (currentDeveloperFilter != null && !currentDeveloperFilter.isEmpty()) {
            // We need to further filter the already filtered list
            List<Game> developerFilteredGames = new ArrayList<>();
            for (Game game : filteredGamesList) {
                String developer = game.getDeveloper();
                if (developer != null && developer.equals(currentDeveloperFilter)) {
                    developerFilteredGames.add(game);
                }
            }
            filteredGamesList = developerFilteredGames;
        }

        // Apply year range filter if active
        if (currentYearRange != null) {
            // We need to further filter the already filtered list
            List<Game> yearFilteredGames = new ArrayList<>();
            int startYear = currentYearRange[0];
            int endYear = currentYearRange[1];

            for (Game game : filteredGamesList) {
                Integer releaseYear = game.getReleaseYear();
                if (releaseYear != null && releaseYear >= startYear && releaseYear <= endYear) {
                    yearFilteredGames.add(game);
                }
            }
            filteredGamesList = yearFilteredGames;
        }

        // Apply sorting if active
        if (currentSort != null) {
            if ("title_asc".equals(currentSort)) {
                filteredGamesList = gameRepository.getGamesSortedByTitleAsc();

                // We need to reapply any filters to the sorted list
                applyFiltersToSortedList();
            } else if ("title_desc".equals(currentSort)) {
                filteredGamesList = gameRepository.getGamesSortedByTitleDesc();

                // We need to reapply any filters to the sorted list
                applyFiltersToSortedList();
            } else if ("year_asc".equals(currentSort)) {
                filteredGamesList = gameRepository.getGamesSortedByYearAsc();

                // We need to reapply any filters to the sorted list
                applyFiltersToSortedList();
            } else if ("year_desc".equals(currentSort)) {
                filteredGamesList = gameRepository.getGamesSortedByYearDesc();

                // We need to reapply any filters to the sorted list
                applyFiltersToSortedList();
            }
        }

        // Reset to page 1 when filters change
        pageNumber = 1;

        // Update the UI
        render(filteredGamesList, game_card_flow, pageNumber);
        page_number.setText(String.valueOf(pageNumber));
    }

    /**
     * Reapplies filters to the sorted list
     */
    private void applyFiltersToSortedList() {
        // Apply genre filter if active
        if (currentGenreFilter != null && !currentGenreFilter.isEmpty()) {
            filteredGamesList.removeIf(game ->
                    game.getGenres() == null || !game.getGenres().contains(currentGenreFilter));
        }

        // Apply platform filter if active
        if (currentPlatformFilter != null && !currentPlatformFilter.isEmpty()) {
            filteredGamesList.removeIf(game ->
                    game.getPlatforms() == null || !game.getPlatforms().contains(currentPlatformFilter));
        }

        // Apply tag filter if active
        if (currentTagFilter != null && !currentTagFilter.isEmpty()) {
            filteredGamesList.removeIf(game ->
                    game.getTags() == null || !game.getTags().contains(currentTagFilter));
        }

        // Apply publisher filter if active
        if (currentPublisherFilter != null && !currentPublisherFilter.isEmpty()) {
            filteredGamesList.removeIf(game -> {
                String publisher = game.getPublisher();
                return publisher == null || !publisher.equals(currentPublisherFilter);
            });
        }

        // Apply developer filter if active
        if (currentDeveloperFilter != null && !currentDeveloperFilter.isEmpty()) {
            filteredGamesList.removeIf(game -> {
                String developer = game.getDeveloper();
                return developer == null || !developer.equals(currentDeveloperFilter);
            });
        }

        // Apply year range filter if active
        if (currentYearRange != null) {
            int startYear = currentYearRange[0];
            int endYear = currentYearRange[1];

            filteredGamesList.removeIf(game -> {
                Integer releaseYear = game.getReleaseYear();
                return releaseYear == null || releaseYear < startYear || releaseYear > endYear;
            });
        }
    }
}