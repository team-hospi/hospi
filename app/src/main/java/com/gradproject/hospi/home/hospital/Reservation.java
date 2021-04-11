package com.gradproject.hospi.home.hospital;

public class Reservation {
    public static final String DB_NAME = "reservationList";

    private String id;
    private String hospitalId;
    private String department;
    private String reservationDate;
    private String reservationTime;
    private String additionalContent;
    private long timestamp;

    public Reservation() {}

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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getAdditionalContent() {
        return additionalContent;
    }

    public void setAdditionalContent(String additionalContent) {
        this.additionalContent = additionalContent;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
