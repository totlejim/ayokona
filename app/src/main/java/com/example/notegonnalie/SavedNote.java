package com.example.notegonnalie;

public class SavedNote {
    private String title;
    private String description;

    public SavedNote(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
}
