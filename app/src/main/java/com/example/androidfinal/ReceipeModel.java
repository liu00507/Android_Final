package com.example.androidfinal;

public class ReceipeModel {
    // Title of receipe
    String title;
    // Href for receipe
    String href;
    // Ingredients fo receipe
    String ingredients;
    // Thumbnail image for receipe
    String thumbnail;

    // Empty Contsructor
    public ReceipeModel() {
    }

    // Constructor will all paramters
    public ReceipeModel(String title, String href, String ingredients, String thumbnail) {
        this.title = title;
        this.href = href;
        this.ingredients = ingredients;
        this.thumbnail = thumbnail;
    }

    // Below are getter and setters of all variables

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}