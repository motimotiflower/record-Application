package com.example.sample3.db.model;

public class Todo {
    private long id;
    private String title;
    private boolean done;
    private long fileId;

    public Todo(long id, String title, boolean done ) {
        this.id = id;
        this.title = title;
        this.done = done;
    }

    public Todo(long id, String title, boolean done,long fileId ) {
        this.id = id;
        this.title = title;
        this.done = done;
        this.fileId = fileId;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public long getFileId() { return fileId; }
    public void setFileId(long fileId) { this.fileId = fileId; }
}
