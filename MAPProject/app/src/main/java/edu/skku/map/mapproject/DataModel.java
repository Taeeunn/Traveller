package edu.skku.map.mapproject;

public class DataModel {
    private String main;
    private String description;
    private String icon;
    private double temp;
    private double feels_like;
    private double temp_min;
    private double temp_max;
    private double humidity;
    private long sunrise;
    private long sunset;
    private long timezone;
    private Double lon;
    private Double lat;

    public Double getLon() {
        return lon;
    }

    public Double getLat() {
        return lat;
    }

    public long getSunrise() {
        return sunrise;
    }


    public long getSunset() {
        return sunset;
    }


    public double getTemp() {
        return temp;
    }


    public double getFeels_like() {
        return feels_like;
    }


    public double getTemp_min() {
        return temp_min;
    }


    public double getTemp_max() {
        return temp_max;
    }


    public double getHumidity() {
        return humidity;
    }


    public String getMain() {
        return main;
    }


    public String getDescription() {
        return description;
    }


    public String getIcon() {
        return icon;
    }

}
