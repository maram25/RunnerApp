package com.example.runner.History;

import com.google.gson.annotations.SerializedName;

public class HistoryModel {

    private int Id=1;
    private double ToLatitude;
    private double ToLongitude;
    private String TimeTotal;
    private int Steps=50;
    private String Time="";
    private String Date;
    private String Distance="150";

    public void setTimeTotal(String timeTotal) {
        TimeTotal = timeTotal;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public double getToLatitude() {
        return ToLatitude;
    }

    public void setToLatitude(double toLatitude) {
        ToLatitude = toLatitude;
    }

    public double getToLongitude() {
        return ToLongitude;
    }

    public void setToLongitude(double toLongitude) {
        ToLongitude = toLongitude;
    }



    public int getSteps() {
        return Steps;
    }

    public void setSteps(int steps) {
        Steps = steps;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }
}
