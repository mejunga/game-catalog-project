package com.example.gamecatalog.controller;

import com.example.gamecatalog.model.Game;
import com.example.gamecatalog.repository.GameRepository;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for the Add Game form.
 */
public class AddGameController {
    @FXML private TextField titleField;
    @FXML private TextField developerField;
    @FXML private TextField publisherField;
    @FXML private TextField genresField;
    @FXML private TextField platformsField;
    @FXML private TextField translatorsField;
    @FXML private TextField steamIdField;
    @FXML private TextField releaseYearField;
    @FXML private TextField languageField;
    @FXML private TextField ratingField;
    @FXML private TextField tagsField;
    @FXML private TextField coverImageField;
    @FXML private TextField descriptionField;
    @FXML private Button browseButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private GameRepository gameRepository;
    private File selectedImageFile;
    private Stage stage;

    /**
     * Initializes the controller.
     */
    public void initialize() {
        gameRepository = new GameRepository();
    }

    /**
     * Sets the stage for this controller.
     *
     * @param stage the stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handles the Browse Image button click.
     */
    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Cover Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        selectedImageFile = fileChooser.showOpenDialog(stage);
        if (selectedImageFile != null) {
            coverImageField.setText(selectedImageFile.getName());
        }
    }

    /**
     * Handles the Save button click.
     */
    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            Game game = createGameFromFields();
            String coverImagePath = processCoverImage();
            
            if (coverImagePath != null) {
                game.setCoverImagePath(coverImagePath);
            }

            gameRepository.addGame(game);
            gameRepository.saveGames();
            
            showAlert(AlertType.INFORMATION, "Success", "Game Added", 
                    "The game has been added successfully.");
            
            closeWindow();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Failed to Save Game", 
                    "An error occurred while saving the game: " + e.getMessage());
        }
    }

    /**
     * Validates form input.
     *
     * @return true if input is valid, false otherwise
     */
    private boolean validateInput() {
        if (titleField.getText().trim().isEmpty() || 
            developerField.getText().trim().isEmpty() || 
            publisherField.getText().trim().isEmpty()) {
            
            showAlert(AlertType.ERROR, "Validation Error", "Missing Required Fields", 
                    "Title, Developer, and Publisher are required fields.");
            return false;
        }

        // Validate release year if provided
        if (!releaseYearField.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(releaseYearField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Validation Error", "Invalid Release Year", 
                        "Release Year must be a valid integer.");
                return false;
            }
        }

        // Validate steam ID if provided
        if (!steamIdField.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(steamIdField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Validation Error", "Invalid Steam ID", 
                        "Steam ID must be a valid integer.");
                return false;
            }
        }

        // Validate rating if provided
        if (!ratingField.getText().trim().isEmpty()) {
            try {
                double rating = Double.parseDouble(ratingField.getText().trim());
                if (rating < 0 || rating > 10) {
                    showAlert(AlertType.ERROR, "Validation Error", "Invalid Rating", 
                            "Rating must be between 0 and 10.");
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Validation Error", "Invalid Rating", 
                        "Rating must be a valid number.");
                return false;
            }
        }

        return true;
    }

    /**
     * Creates a Game object from form fields.
     *
     * @return the created Game object
     */
    private Game createGameFromFields() {
        String title = titleField.getText().trim();
        String developer = developerField.getText().trim();
        String publisher = publisherField.getText().trim();

        Game game = new Game(title, developer, publisher);

        // Set optional fields if provided
        if (!genresField.getText().trim().isEmpty()) {
            game.setGenres(parseCSVList(genresField.getText()));
        }

        if (!platformsField.getText().trim().isEmpty()) {
            game.setPlatforms(parseCSVList(platformsField.getText()));
        }

        if (!translatorsField.getText().trim().isEmpty()) {
            game.setTranslators(parseCSVList(translatorsField.getText()));
        }

        if (!steamIdField.getText().trim().isEmpty()) {
            game.setSteamId(Integer.parseInt(steamIdField.getText().trim()));
        }

        if (!releaseYearField.getText().trim().isEmpty()) {
            game.setReleaseYear(Integer.parseInt(releaseYearField.getText().trim()));
        }

        if (!languageField.getText().trim().isEmpty()) {
            game.setLanguage(languageField.getText().trim());
        }

        if (!ratingField.getText().trim().isEmpty()) {
            game.setRating(Double.parseDouble(ratingField.getText().trim()));
        }

        if (!tagsField.getText().trim().isEmpty()) {
            game.setTags(parseCSVList(tagsField.getText()));
        }

        if (!descriptionField.getText().trim().isEmpty()) {
            game.setDescriptionPath(descriptionField.getText().trim());
        }

        return game;
    }

    /**
     * Parses a comma-separated values string into a list of strings.
     *
     * @param csv the CSV string
     * @return the list of strings
     */
    private List<String> parseCSVList(String csv) {
        if (csv == null || csv.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Processes the cover image by copying it to the resources/images directory.
     *
     * @return the path to the saved image relative to the resources folder, or null if no image was selected
     * @throws IOException if an I/O error occurs
     */
    private String processCoverImage() throws IOException {
        if (selectedImageFile == null) {
            return null;
        }

        // Create images directory if it doesn't exist
        Path imagesDir = Paths.get("src/main/resources/images");
        if (!Files.exists(imagesDir)) {
            Files.createDirectories(imagesDir);
        }

        // Generate unique filename to avoid collisions
        String fileExtension = getFileExtension(selectedImageFile.getName());
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        Path targetPath = imagesDir.resolve(uniqueFileName);

        // Copy the image file to the resources/images directory
        Files.copy(selectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Return the path relative to the resources folder
        return "images/" + uniqueFileName;
    }

    /**
     * Gets the file extension from a filename.
     *
     * @param filename the filename
     * @return the file extension including the dot, or an empty string if there is no extension
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }

    /**
     * Shows an alert dialog.
     *
     * @param alertType the alert type
     * @param title the alert title
     * @param headerText the alert header text
     * @param contentText the alert content text
     */
    private void showAlert(AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Handles the Cancel button click.
     */
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    /**
     * Closes the window.
     */
    private void closeWindow() {
        if (stage != null) {
            stage.close();
        }
    }
} 