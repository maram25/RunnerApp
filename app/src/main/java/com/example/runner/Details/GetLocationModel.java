package com.example.runner.Details;

public class GetLocationModel {


    private double Lat;
    private double Lng;

    public GetLocationModel(double lat, double lng, String address) {
        Lat = lat;
        Lng = lng;
        Address = address;
    }

    public double getLat() {
        return Lat;
    }

    public double getLng() {
        return Lng;
    }

    public String getAddress() {
        return Address;
    }

    private String Address;
}
