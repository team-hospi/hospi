package com.gradproject.hospi;

import java.io.Serializable;

public class Inquiry implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String DB_NAME = "inquiryList";

    private String documentId;
    private String id;
    private String hospitalId;
    private String hospitalName;
    private long timestamp;
    private String title;
    private String content;
    private String answer;
    private boolean checkedAnswer;

    public Inquiry(){}

    public Inquiry(String id, String hospitalId, String hospitalName, long timestamp, String title, String content, String answer, boolean checkedAnswer) {
        this.id = id;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
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

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
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
