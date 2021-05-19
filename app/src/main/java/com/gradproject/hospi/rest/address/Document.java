package com.gradproject.hospi.rest.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Document {

    @SerializedName("address")
    @Expose
    private Address address;
    @SerializedName("address_name")
    @Expose
    private String addressName;
    @SerializedName("address_type")
    @Expose
    private String addressType;
    @SerializedName("road_address")
    @Expose
    private RoadAddress roadAddress;
    @SerializedName("x")
    @Expose
    private String x;
    @SerializedName("y")
    @Expose
    private String y;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public RoadAddress getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(RoadAddress roadAddress) {
        this.roadAddress = roadAddress;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

}