package com.sweteam5.ladybugadmin;

public class Station {
    private String name;        // Name of the station
    private int index;          // Index of the station
    private double latitude;    // Latitude of the station
    private double longitude;   // Longitude of the station

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
