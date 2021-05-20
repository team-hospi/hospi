package com.gradproject.hospi.home;

import androidx.annotation.NonNull;

public class LocationPoint {
    // 위도
    public double longitude;
    // 경도
    public double latitude;
    public String addr;
    // 포인트를 받았는지 여부
    public boolean havePoint;

    @NonNull
    @Override
    public String toString() {

        return "x : " +
                longitude +
                " y : " +
                latitude +
                " addr : " +
                addr;
    }
}
