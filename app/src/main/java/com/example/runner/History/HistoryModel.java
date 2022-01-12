package com.example.runner.History;

import com.google.gson.annotations.SerializedName;

public class HistoryModel {

    private int Id;
    private double ToLatitude;
    private double ToLongitude;
    private int TimeTotal;
    private int Steps;
    private int Time;
    private String Date;
    private String Distance;

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

    public int getTimeTotal() {
        return TimeTotal;
    }

    public void setTimeTotal(int timeTotal) {
        TimeTotal = timeTotal;
    }

    public int getSteps() {
        return Steps;
    }

    public void setSteps(int steps) {
        Steps = steps;
    }

    public int getTime() {
        return Time;
    }

    public void setTime(int time) {
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
