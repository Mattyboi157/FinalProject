package com.example.finalproject;

public class SavedImage {
    private String title;
    private String description;
    private String imageUrl;
    private String hdUrl;
    private String date;

    public SavedImage() {
    }
    public SavedImage(String title, String description, String imageUrl, String hdUrl, String date) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.hdUrl = hdUrl;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getHdUrl() {
        return hdUrl;
    }

    public String getDate() {
        return date;
    }
}
