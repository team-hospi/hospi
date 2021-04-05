package com.gradproject.hospi.home.search;

import java.io.Serializable;
import java.util.List;

public class Hospital implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String DB_NAME = "hospitals";

    private String id; // 병원 아이디
    private String name; // 병원 이름
    private List<String> department; // 병원 진료과 목록
    private String address; // 병원 주소
    private String tel; // 병원 전화번호
    private String kind; // 병원 종류 (의원, 종합병원, 대학병원)
    private String lunchTime; // 점심 시간 시작 (1시간)
    private String weekdayOpen; // 평일 영업 시작 시간
    private String weekdayClose; // 평일 영업 종료 시간
    private String saturdayOpen; // 토요일 영업 시작 시간
    private String saturdayClose; // 토요일 영업 종료 시간
    private String holidayOpen; // 공휴일 영업 시작 시간
    private String holidayClose; // 공휴일 영업 종료 시간
    private boolean status; // 영업 여부
    private boolean saturdayStatus; // 토요일 영업 여부
    private boolean holidayStatus; // 공휴일 영업 여부
    private boolean todayReservation; // 금일 예약 가능 여부

    public Hospital() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDepartment() {
        return department;
    }

    public void setDepartment(List<String> department) {
        this.department = department;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getLunchTime() {
        return lunchTime;
    }

    public void setLunchTime(String lunchTime) {
        this.lunchTime = lunchTime;
    }

    public String getWeekdayOpen() {
        return weekdayOpen;
    }

    public void setWeekdayOpen(String weekdayOpen) {
        this.weekdayOpen = weekdayOpen;
    }

    public String getWeekdayClose() {
        return weekdayClose;
    }

    public void setWeekdayClose(String weekdayClose) {
        this.weekdayClose = weekdayClose;
    }

    public String getSaturdayOpen() {
        return saturdayOpen;
    }

    public void setSaturdayOpen(String saturdayOpen) {
        this.saturdayOpen = saturdayOpen;
    }

    public String getSaturdayClose() {
        return saturdayClose;
    }

    public void setSaturdayClose(String saturdayClose) {
        this.saturdayClose = saturdayClose;
    }

    public String getHolidayOpen() {
        return holidayOpen;
    }

    public void setHolidayOpen(String holidayOpen) {
        this.holidayOpen = holidayOpen;
    }

    public String getHolidayClose() {
        return holidayClose;
    }

    public void setHolidayClose(String holidayClose) {
        this.holidayClose = holidayClose;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isSaturdayStatus() {
        return saturdayStatus;
    }

    public void setSaturdayStatus(boolean saturdayStatus) {
        this.saturdayStatus = saturdayStatus;
    }

    public boolean isHolidayStatus() {
        return holidayStatus;
    }

    public void setHolidayStatus(boolean holidayStatus) {
        this.holidayStatus = holidayStatus;
    }

    public boolean isTodayReservation() {
        return todayReservation;
    }

    public void setTodayReservation(boolean todayReservation) {
        this.todayReservation = todayReservation;
    }
}
