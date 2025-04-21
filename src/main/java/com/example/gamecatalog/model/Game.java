package com.example.gamecatalog.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private String title;
    private String developer;
    private String publisher;
    private List<String> genres;
    private List<String> platforms;
    private List<String> translators;
    private Integer steamId;
    private Integer releaseYear;
    private String language;
    private Double rating;
    private List<String> tags;
    private String coverImagePath;
    private String descrptionPath;

    // Default constructor for Jackson
    public Game() {
        this.genres = new ArrayList<>();
        this.platforms = new ArrayList<>();
        this.translators = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    // Constructor with required fields
    public Game(String title, String developer, String publisher) {
        this();
        this.title = title;
        this.developer = developer;
        this.publisher = publisher;
    }

    // Full constructor
    public Game(String title, String developer, String publisher,
                List<String> genres, List<String> platforms, List<String> translators,
                Integer steamId, Integer releaseYear, String language, Double rating,
                List<String> tags, String coverImagePath, String descrptionPath) {
        this.title = title;
        this.developer = developer;
        this.publisher = publisher;
        this.genres = genres != null ? new ArrayList<>(genres) : new ArrayList<>();
        this.platforms = platforms != null ? new ArrayList<>(platforms) : new ArrayList<>();
        this.translators = translators != null ? new ArrayList<>(translators) : new ArrayList<>();
        this.steamId = steamId;
        this.releaseYear = releaseYear;
        this.language = language;
        this.rating = rating;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.coverImagePath = coverImagePath;
        this.descrptionPath = descrptionPath;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres != null ? new ArrayList<>(genres) : new ArrayList<>();
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms != null ? new ArrayList<>(platforms) : new ArrayList<>();
    }

    public List<String> getTranslators() {
        return translators;
    }

    public void setTranslators(List<String> translators) {
        this.translators = translators != null ? new ArrayList<>(translators) : new ArrayList<>();
    }

    public Integer getSteamId() {
        return steamId;
    }

    public void setSteamId(Integer steamId) {
        this.steamId = steamId;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }

    public String getDescriptionPath() {
        return descrptionPath;
    }

    public void setDescriptionPath(String descriptionPath) {
        this.descrptionPath = descriptionPath;
    }
}