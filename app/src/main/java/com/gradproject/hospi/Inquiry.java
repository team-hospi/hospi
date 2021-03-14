package com.gradproject.hospi;

import java.io.Serializable;

public class Inquiry implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String hospital_id;
    private String hospital_name;
    private String date;
    private String title;
    private String content;
    private String answer;

    public Inquiry(){}

    public Inquiry(String id, String hospital_id, String hospital_name, String date, String title, String content, String answer) {
        this.id = id;
        this.hospital_id = hospital_id;
        this.hospital_name = hospital_name;
        this.date = date;
        this.title = title;
        this.content = content;
        this.answer = answer;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}
