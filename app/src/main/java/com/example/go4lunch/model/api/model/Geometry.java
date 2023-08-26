package com.example.go4lunch.model.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Geometry implements Serializable {

    @SerializedName("location")
    @Expose
    private Location location;

    // Empty constructor to allow firebase to cast document to object model
    public Geometry() {}

    // Constructor
    public Geometry(Location location) {
        this.location = location;
    }


    // GETTERS

    public Location getLocation() {
        return location;
    }


    // SETTERS

    public void setLocation(Location location) {
        this.location = location;
    }

}
