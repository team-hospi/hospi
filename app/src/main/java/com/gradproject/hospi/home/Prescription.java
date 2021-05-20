package com.gradproject.hospi.home;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unused")
public class Prescription implements Serializable {
    public static final String DB_NAME = "prescriptionList";
    private static final long serialVersionUID = 1L;

    private String department;
    private String hospitalId;
    private String hospitalName;
    private String id;
    private List<String> medicine;
    private String opinion;
    private long timestamp;

    public Prescription() {
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getMedicine() {
        return medicine;
    }

    public void setMedicine(List<String> medicine) {
        this.medicine = medicine;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
