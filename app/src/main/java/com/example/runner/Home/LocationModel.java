package com.example.runner.Home;

public class LocationModel {

    private double Lat;
    private double Lng;

    public LocationModel(double lat, double lng) {
        Lat = lat;
        Lng = lng;
    }


    public double getLat() {
        return Lat;
    }

    public double getLng() {
        return Lng;
    }
}
