package com.gradproject.hospi.home;

public class LocationPoint {
    // 위도
    public double longitude;
    // 경도
    public double latitude;
    public String addr;
    // 포인트를 받았는지 여부
    public boolean havePoint;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("x : ");
        builder.append(longitude);
        builder.append(" y : ");
        builder.append(latitude);
        builder.append(" addr : ");
        builder.append(addr);

        return builder.toString();
    }
}
