package com.example.gamecatalog.repository;

import com.example.gamecatalog.model.Game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Repository class for managing game data from JSON file.
 * Reads and stores game objects from data/games_all.json.
 */
public class GameRepository {
    private static final Logger LOGGER = Logger.getLogger(GameRepository.class.getName());
    private static final String JSON_FILE_PATH = "data/games_all.json";

    private List<Game> games;

    /**
     * Constructor initializes the repository and loads games from JSON.
     */
    public GameRepository() {
        this.games = new ArrayList<>();
        loadGames();
    }

    /**
     * Loads games from the JSON file.
     */
    private void loadGames() {
        games = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            parseGamesFromJson(content);
            LOGGER.info("Successfully loaded " + games.size() + " games from " + JSON_FILE_PATH);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading games from JSON file", e);
        }
    }

    /**
     * Parses the JSON content into Game objects.
     * Note: This is a simplified parser and may not handle all JSON cases.
     *
     * @param content JSON content as string
     */
    private void parseGamesFromJson(String content) {
        // Extract individual game objects
        Pattern gamePattern = Pattern.compile("\\{[^{}]*(?:\\{[^{}]*\\}[^{}]*)*\\}");
        Matcher gameMatcher = gamePattern.matcher(content);

        while (gameMatcher.find()) {
            String gameJson = gameMatcher.group();
            Game game = parseGameFromJson(gameJson);
            if (game != null) {
                games.add(game);
            }
        }
    }

    /**
     * Parses a single game JSON object into a Game object.
     *
     * @param gameJson the JSON string for a single game
     * @return the Game object or null if parsing failed
     */
    private Game parseGameFromJson(String gameJson) {
        Game game = new Game();

        // Extract title
        extractStringValue(gameJson, "title", game::setTitle);

        // Extract developer
        extractStringValue(gameJson, "developer", game::setDeveloper);

        // Extract publisher
        extractStringValue(gameJson, "publisher", game::setPublisher);

        // Extract genres
        extractStringListValue(gameJson, "genres", game::setGenres);

        // Extract platforms
        extractStringListValue(gameJson, "platforms", game::setPlatforms);

        // Extract translators
        extractStringListValue(gameJson, "translators", game::setTranslators);

        // Extract steamId
        extractIntegerValue(gameJson, "steamId", game::setSteamId);

        // Extract releaseYear
        extractIntegerValue(gameJson, "releaseYear", game::setReleaseYear);

        // Extract language
        extractStringValue(gameJson, "language", game::setLanguage);

        // Extract rating
        extractDoubleValue(gameJson, "rating", game::setRating);

        // Extract tags
        extractStringListValue(gameJson, "tags", game::setTags);

        // Extract coverImagePath
        extractStringValue(gameJson, "coverImagePath", game::setCoverImagePath);

        // Extract descriptionPath (correcting the misspelling in the JSON)
        extractStringValue(gameJson, "descriptionPath", game::setDescriptionPath);

        return game;
    }

    /**
     * Extracts a string value from JSON.
     *
     * @param json the JSON string
     * @param fieldName the field name to extract
     * @param setter the setter method to call with the extracted value
     */
    private void extractStringValue(String json, String fieldName, StringSetter setter) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            String value = matcher.group(1);
            if (!"null".equals(value)) {
                setter.set(value);
            }
        }
    }

    /**
     * Extracts an integer value from JSON.
     *
     * @param json the JSON string
     * @param fieldName the field name to extract
     * @param setter the setter method to call with the extracted value
     */
    private void extractIntegerValue(String json, String fieldName, IntegerSetter setter) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*(\\d+|null)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            String value = matcher.group(1);
            if (!"null".equals(value)) {
                setter.set(Integer.parseInt(value));
            }
        }
    }

    /**
     * Extracts a double value from JSON.
     *
     * @param json the JSON string
     * @param fieldName the field name to extract
     * @param setter the setter method to call with the extracted value
     */
    private void extractDoubleValue(String json, String fieldName, DoubleSetter setter) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*(\\d+\\.\\d+|\\d+|null)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            String value = matcher.group(1);
            if (!"null".equals(value)) {
                setter.set(Double.parseDouble(value));
            }
        }
    }

    /**
     * Extracts a string list value from JSON.
     *
     * @param json the JSON string
     * @param fieldName the field name to extract
     * @param setter the setter method to call with the extracted value
     */
    private void extractStringListValue(String json, String fieldName, ListSetter setter) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            String listContent = matcher.group(1);
            List<String> items = new ArrayList<>();
            Pattern itemPattern = Pattern.compile("\"([^\"]*)\"");
            Matcher itemMatcher = itemPattern.matcher(listContent);
            while (itemMatcher.find()) {
                items.add(itemMatcher.group(1));
            }
            setter.set(items);
        }
    }

    /**
     * Functional interface for setting string values.
     */
    @FunctionalInterface
    private interface StringSetter {
        void set(String value);
    }

    /**
     * Functional interface for setting integer values.
     */
    @FunctionalInterface
    private interface IntegerSetter {
        void set(Integer value);
    }

    /**
     * Functional interface for setting double values.
     */
    @FunctionalInterface
    private interface DoubleSetter {
        void set(Double value);
    }

    /**
     * Functional interface for setting list values.
     */
    @FunctionalInterface
    private interface ListSetter {
        void set(List<String> value);
    }

    /**
     * Saves the current list of games to the JSON file.
     *
     * @return true if saved successfully, false otherwise
     */
    public boolean saveGames() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(JSON_FILE_PATH))) {
            writer.write("[\n");
            for (int i = 0; i < games.size(); i++) {
                writer.write(gameToJson(games.get(i)));
                if (i < games.size() - 1) {
                    writer.write(",\n");
                }
            }
            writer.write("\n]");
            LOGGER.info("Successfully saved " + games.size() + " games to " + JSON_FILE_PATH);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving games to JSON file", e);
            return false;
        }
    }

    /**
     * Converts a Game object to JSON string.
     *
     * @param game the game to convert
     * @return JSON string representation of the game
     */
    private String gameToJson(Game game) {
        StringBuilder sb = new StringBuilder();
        sb.append("  {\n");

        // Add title
        appendJsonString(sb, "title", game.getTitle(), true);

        // Add developer
        appendJsonString(sb, "developer", game.getDeveloper(), true);

        // Add publisher
        appendJsonString(sb, "publisher", game.getPublisher(), true);

        // Add genres
        appendJsonArray(sb, "genres", game.getGenres(), true);

        // Add platforms
        appendJsonArray(sb, "platforms", game.getPlatforms(), true);

        // Add translators
        appendJsonArray(sb, "translators", game.getTranslators(), true);

        // Add steamId
        appendJsonValue(sb, "steamId", game.getSteamId(), true);

        // Add releaseYear
        appendJsonValue(sb, "releaseYear", game.getReleaseYear(), true);

        // Add language
        appendJsonString(sb, "language", game.getLanguage(), true);

        // Add rating
        appendJsonValue(sb, "rating", game.getRating(), true);

        // Add tags
        appendJsonArray(sb, "tags", game.getTags(), true);

        // Add coverImagePath
        appendJsonString(sb, "coverImagePath", game.getCoverImagePath(), true);

        // Add descriptionPath (correcting the misspelling in the JSON)
        appendJsonString(sb, "descriptionPath", game.getDescriptionPath(), false);

        sb.append("\n  }");
        return sb.toString();
    }

    /**
     * Appends a JSON string field to the StringBuilder.
     *
     * @param sb the StringBuilder to append to
     * @param fieldName the field name
     * @param value the value
     * @param addComma whether to add a comma after the field
     */
    private void appendJsonString(StringBuilder sb, String fieldName, String value, boolean addComma) {
        sb.append("    \"").append(fieldName).append("\" : ");
        if (value == null) {
            sb.append("null");
        } else {
            sb.append("\"").append(value).append("\"");
        }
        if (addComma) {
            sb.append(",");
        }
        sb.append("\n");
    }

    /**
     * Appends a JSON value field to the StringBuilder.
     *
     * @param sb the StringBuilder to append to
     * @param fieldName the field name
     * @param value the value
     * @param addComma whether to add a comma after the field
     */
    private void appendJsonValue(StringBuilder sb, String fieldName, Object value, boolean addComma) {
        sb.append("    \"").append(fieldName).append("\" : ");
        if (value == null) {
            sb.append("null");
        } else {
            sb.append(value);
        }
        if (addComma) {
            sb.append(",");
        }
        sb.append("\n");
    }

    /**
     * Appends a JSON array field to the StringBuilder.
     *
     * @param sb the StringBuilder to append to
     * @param fieldName the field name
     * @param list the list of values
     * @param addComma whether to add a comma after the field
     */
    private void appendJsonArray(StringBuilder sb, String fieldName, List<String> list, boolean addComma) {
        sb.append("    \"").append(fieldName).append("\" : [");
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                sb.append(" \"").append(list.get(i)).append("\"");
                if (i < list.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append(" ");
        }
        sb.append("]");
        if (addComma) {
            sb.append(",");
        }
        sb.append("\n");
    }

    /**
     * Returns all games in the repository.
     *
     * @return the list of games
     */
    public List<Game> getAllGames() {
        return new ArrayList<>(games);
    }

    /**
     * Adds a new game to the repository.
     *
     * @param game the game to add
     * @return true if added successfully
     */
    public boolean addGame(Game game) {
        if (game == null) {
            return false;
        }
        games.add(game);
        return true;
    }

    /**
     * Updates an existing game in the repository.
     *
     * @param index the index of the game to update
     * @param game the updated game data
     * @return true if updated successfully, false if index is invalid
     */
    public boolean updateGame(int index, Game game) {
        if (game == null || index < 0 || index >= games.size()) {
            return false;
        }
        games.set(index, game);
        return true;
    }

    /**
     * Removes a game from the repository.
     *
     * @param index the index of the game to remove
     * @return true if removed successfully, false if index is invalid
     */
    public boolean removeGame(int index) {
        if (index < 0 || index >= games.size()) {
            return false;
        }
        games.remove(index);
        return true;
    }

    /**
     * Gets the number of games in the repository.
     *
     * @return the number of games
     */
    public int getGameCount() {
        return games.size();
    }

    /**
     * Gets all unique genres from the games in the repository.
     *
     * @return a list of unique genres
     */
    public List<String> getAllGenres() {
        List<String> allGenres = new ArrayList<>();

        // Collect all genres from all games
        for (Game game : games) {
            List<String> genres = game.getGenres();
            if (genres != null) {
                for (String genre : genres) {
                    if (!allGenres.contains(genre)) {
                        allGenres.add(genre);
                    }
                }
            }
        }

        // Sort alphabetically for better user experience
        allGenres.sort(String::compareToIgnoreCase);

        return allGenres;
    }

    /**
     * Gets all games that have the specified genre.
     *
     * @param genre the genre to filter by
     * @return a list of games with the specified genre
     */
    public List<Game> getGamesByGenre(String genre) {
        if (genre == null || genre.isEmpty()) {
            return new ArrayList<>(games); // Return all games if no genre specified
        }

        List<Game> filteredGames = new ArrayList<>();

        for (Game game : games) {
            List<String> genres = game.getGenres();
            if (genres != null && genres.contains(genre)) {
                filteredGames.add(game);
            }
        }

        return filteredGames;
    }

    /**
     * Gets all unique platforms from the games in the repository.
     *
     * @return a list of unique platforms
     */
    public List<String> getAllPlatforms() {
        List<String> allPlatforms = new ArrayList<>();

        // Collect all platforms from all games
        for (Game game : games) {
            List<String> platforms = game.getPlatforms();
            if (platforms != null) {
                for (String platform : platforms) {
                    if (!allPlatforms.contains(platform)) {
                        allPlatforms.add(platform);
                    }
                }
            }
        }

        // Sort alphabetically for better user experience
        allPlatforms.sort(String::compareToIgnoreCase);

        return allPlatforms;
    }

    /**
     * Gets all unique tags from the games in the repository.
     *
     * @return a list of unique tags
     */
    public List<String> getAllTags() {
        List<String> allTags = new ArrayList<>();

        // Collect all tags from all games
        for (Game game : games) {
            List<String> tags = game.getTags();
            if (tags != null) {
                for (String tag : tags) {
                    if (!allTags.contains(tag)) {
                        allTags.add(tag);
                    }
                }
            }
        }

        // Sort alphabetically for better user experience
        allTags.sort(String::compareToIgnoreCase);

        return allTags;
    }

    /**
     * Gets all games that have the specified platform.
     *
     * @param platform the platform to filter by
     * @return a list of games with the specified platform
     */
    public List<Game> getGamesByPlatform(String platform) {
        if (platform == null || platform.isEmpty()) {
            return new ArrayList<>(games); // Return all games if no platform specified
        }

        List<Game> filteredGames = new ArrayList<>();

        for (Game game : games) {
            List<String> platforms = game.getPlatforms();
            if (platforms != null && platforms.contains(platform)) {
                filteredGames.add(game);
            }
        }

        return filteredGames;
    }

    /**
     * Gets all games that have the specified tag.
     *
     * @param tag the tag to filter by
     * @return a list of games with the specified tag
     */
    public List<Game> getGamesByTag(String tag) {
        if (tag == null || tag.isEmpty()) {
            return new ArrayList<>(games); // Return all games if no tag specified
        }

        List<Game> filteredGames = new ArrayList<>();

        for (Game game : games) {
            List<String> tags = game.getTags();
            if (tags != null && tags.contains(tag)) {
                filteredGames.add(game);
            }
        }

        return filteredGames;
    }

    /**
     * Gets all unique publishers from the games in the repository.
     *
     * @return a list of unique publishers
     */
    public List<String> getAllPublishers() {
        List<String> allPublishers = new ArrayList<>();

        // Collect all publishers from all games
        for (Game game : games) {
            String publisher = game.getPublisher();
            if (publisher != null && !publisher.isEmpty() && !allPublishers.contains(publisher)) {
                allPublishers.add(publisher);
            }
        }

        // Sort alphabetically for better user experience
        allPublishers.sort(String::compareToIgnoreCase);

        return allPublishers;
    }

    /**
     * Gets all unique developers from the games in the repository.
     *
     * @return a list of unique developers
     */
    public List<String> getAllDevelopers() {
        List<String> allDevelopers = new ArrayList<>();

        // Collect all developers from all games
        for (Game game : games) {
            String developer = game.getDeveloper();
            if (developer != null && !developer.isEmpty() && !allDevelopers.contains(developer)) {
                allDevelopers.add(developer);
            }
        }

        // Sort alphabetically for better user experience
        allDevelopers.sort(String::compareToIgnoreCase);

        return allDevelopers;
    }

    /**
     * Gets the maximum release year from all games.
     *
     * @return the maximum release year or current year if none found
     */
    public int getMaxReleaseYear() {
        int maxYear = java.time.Year.now().getValue(); // Default to current year

        for (Game game : games) {
            Integer releaseYear = game.getReleaseYear();
            if (releaseYear != null && releaseYear > maxYear) {
                maxYear = releaseYear;
            }
        }

        return maxYear;
    }

    /**
     * Gets the minimum release year from all games.
     *
     * @return the minimum release year or 1970 if none found
     */
    public int getMinReleaseYear() {
        int minYear = 2025; // Start with a high value

        for (Game game : games) {
            Integer releaseYear = game.getReleaseYear();
            if (releaseYear != null && releaseYear < minYear) {
                minYear = releaseYear;
            }
        }

        // If no release years were found, default to 1970
        if (minYear == 2025) {
            minYear = 1970;
        }

        return minYear;
    }

    /**
     * Gets all games that have the specified publisher.
     *
     * @param publisher the publisher to filter by
     * @return a list of games with the specified publisher
     */
    public List<Game> getGamesByPublisher(String publisher) {
        if (publisher == null || publisher.isEmpty()) {
            return new ArrayList<>(games); // Return all games if no publisher specified
        }

        List<Game> filteredGames = new ArrayList<>();

        for (Game game : games) {
            String gamePublisher = game.getPublisher();
            if (gamePublisher != null && gamePublisher.equals(publisher)) {
                filteredGames.add(game);
            }
        }

        return filteredGames;
    }

    /**
     * Gets all games that have the specified developer.
     *
     * @param developer the developer to filter by
     * @return a list of games with the specified developer
     */
    public List<Game> getGamesByDeveloper(String developer) {
        if (developer == null || developer.isEmpty()) {
            return new ArrayList<>(games); // Return all games if no developer specified
        }

        List<Game> filteredGames = new ArrayList<>();

        for (Game game : games) {
            String gameDeveloper = game.getDeveloper();
            if (gameDeveloper != null && gameDeveloper.equals(developer)) {
                filteredGames.add(game);
            }
        }

        return filteredGames;
    }

    /**
     * Gets all games that have a release year within the specified range (inclusive).
     *
     * @param startYear the start year (lower bound)
     * @param endYear the end year (upper bound)
     * @return a list of games with release years within the specified range
     */
    public List<Game> getGamesByYearRange(int startYear, int endYear) {
        List<Game> filteredGames = new ArrayList<>();

        for (Game game : games) {
            Integer releaseYear = game.getReleaseYear();
            if (releaseYear != null && releaseYear >= startYear && releaseYear <= endYear) {
                filteredGames.add(game);
            }
        }

        return filteredGames;
    }

    /**
     * Gets all games sorted by title in ascending order (A-Z).
     *
     * @return a list of games sorted by title in ascending order
     */
    public List<Game> getGamesSortedByTitleAsc() {
        List<Game> sortedGames = new ArrayList<>(games);
        sortedGames.sort((g1, g2) -> {
            if (g1.getTitle() == null && g2.getTitle() == null) {
                return 0;
            } else if (g1.getTitle() == null) {
                return -1;
            } else if (g2.getTitle() == null) {
                return 1;
            }
            return g1.getTitle().compareToIgnoreCase(g2.getTitle());
        });
        return sortedGames;
    }

    /**
     * Gets all games sorted by title in descending order (Z-A).
     *
     * @return a list of games sorted by title in descending order
     */
    public List<Game> getGamesSortedByTitleDesc() {
        List<Game> sortedGames = new ArrayList<>(games);
        sortedGames.sort((g1, g2) -> {
            if (g1.getTitle() == null && g2.getTitle() == null) {
                return 0;
            } else if (g1.getTitle() == null) {
                return 1;
            } else if (g2.getTitle() == null) {
                return -1;
            }
            return g2.getTitle().compareToIgnoreCase(g1.getTitle());
        });
        return sortedGames;
    }
}