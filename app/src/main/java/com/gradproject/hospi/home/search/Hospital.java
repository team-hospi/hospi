package com.gradproject.hospi.home.search;

import java.io.Serializable;
import java.util.List;

public class Hospital implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id; // 병원 아이디
    private String name; // 병원 이름
    private List<String> department; // 병원 진료과 목록
    private String address; // 병원 주소
    private String tel; // 병원 전화번호
    private String kind; // 병원 종류 (의원, 종합병원, 대학병원)
    private String weekday_open; // 평일 영업 시작 시간 (예: 0900 -> 오전 9시)
    private String weekday_close; // 평일 영업 종료 시간
    private String saturday_open; // 토요일 영업 시작 시간
    private String saturday_close; // 토요일 영업 종료 시간
    private String holiday_open; // 공휴일 영업 시작 시간
    private String holiday_close; // 공휴일 영업 종료 시간
    private boolean status; // 영업 여부
    private boolean saturday_status; // 토요일 영업 여부
    private boolean holiday_status; // 공휴일 영업 여부
    private boolean today_reservation; // 금일 예약 가능 여부

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

    public String getWeekday_open() {
        return weekday_open;
    }

    public void setWeekday_open(String weekday_open) {
        this.weekday_open = weekday_open;
    }

    public String getWeekday_close() {
        return weekday_close;
    }

    public void setWeekday_close(String weekday_close) {
        this.weekday_close = weekday_close;
    }

    public String getSaturday_open() {
        return saturday_open;
    }

    public void setSaturday_open(String saturday_open) {
        this.saturday_open = saturday_open;
    }

    public String getSaturday_close() {
        return saturday_close;
    }

    public void setSaturday_close(String saturday_close) {
        this.saturday_close = saturday_close;
    }

    public String getHoliday_open() {
        return holiday_open;
    }

    public void setHoliday_open(String holiday_open) {
        this.holiday_open = holiday_open;
    }

    public String getHoliday_close() {
        return holiday_close;
    }

    public void setHoliday_close(String holiday_close) {
        this.holiday_close = holiday_close;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isSaturday_status() {
        return saturday_status;
    }

    public void setSaturday_status(boolean saturday_status) {
        this.saturday_status = saturday_status;
    }

    public boolean isHoliday_status() {
        return holiday_status;
    }

    public void setHoliday_status(boolean holiday_status) {
        this.holiday_status = holiday_status;
    }

    public boolean isToday_reservation() {
        return today_reservation;
    }

    public void setToday_reservation(boolean today_reservation) {
        this.today_reservation = today_reservation;
    }
}
