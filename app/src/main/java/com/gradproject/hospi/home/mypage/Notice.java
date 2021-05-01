package com.gradproject.hospi.home.mypage;

import java.io.Serializable;

public class Notice implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String DB_NAME = "noticeList";

    private String title;
    private String content;
    private long timestamp;
    private String documentId;

    public Notice() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
