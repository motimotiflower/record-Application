package com.example.sample3.db.model;

public class Memo {

    //メンバ
    private long id,fileId;
    private String title;
    private String content;

    //引数ない時
    public Memo() {
    }

    public Memo(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() { return id; }
    public long getFileId() { return fileId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }

    public void setId(long id) { this.id = id; }
    public void setFileId(long fileId) { this.fileId = fileId; }

    //
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

}