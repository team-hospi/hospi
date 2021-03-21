package com.gradproject.hospi;

import java.io.Serializable;

public class Inquiry implements Serializable {
    private static final long serialVersionUID = 1L;

    private String documentId;
    private String id;
    private String hospital_id;
    private String hospital_name;
    private long timestamp;
    private String title;
    private String content;
    private String answer;
    private boolean checkedAnswer;

    public Inquiry(){}

    public Inquiry(String id, String hospital_id, String hospital_name, long timestamp, String title, String content, String answer, boolean checkedAnswer) {
        this.id = id;
        this.hospital_id = hospital_id;
        this.hospital_name = hospital_name;
        this.timestamp = timestamp;
        this.title = title;
        this.content = content;
        this.answer = answer;
        this.checkedAnswer = checkedAnswer;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHospital_id() {
        return hospital_id;
    }

    public void setHospital_id(String hospital_id) {
        this.hospital_id = hospital_id;
    }

    public String getHospital_name() {
        return hospital_name;
    }

    public void setHospital_name(String hospital_name) {
        this.hospital_name = hospital_name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isCheckedAnswer() {
        return checkedAnswer;
    }

    public void setCheckedAnswer(boolean checkedAnswer) {
        this.checkedAnswer = checkedAnswer;
    }
}
