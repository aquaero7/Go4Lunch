package com.example.go4lunch.model.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Location implements Serializable {

    @SerializedName("lat")
    @Expose
    private double lat;

    @SerializedName("lng")
    @Expose
    private double lng;

    // Empty constructor to allow firebase to cast document to object model
    public Location() {}

    // Constructor
    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }


    // GETTERS

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }


    // SETTERS

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

}
