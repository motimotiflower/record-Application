package com.example.sample3.db.model;

public class File {
    private long id;
    private String title;

    public File(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }

    public void setId(long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
}
