package com.gradproject.hospi.home.hospital;

import java.util.List;
import java.util.Map;

public class Reserved {
    public static final String DB_NAME = "reservedList";

    private String department;
    private String hospitalId;
    private Map<String, List<String>> reservedMap;

    public Reserved() {}

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

    public Map<String, List<String>> getReservedMap() {
        return reservedMap;
    }

    public void setReservedMap(Map<String, List<String>> reservedMap) {
        this.reservedMap = reservedMap;
    }
}
