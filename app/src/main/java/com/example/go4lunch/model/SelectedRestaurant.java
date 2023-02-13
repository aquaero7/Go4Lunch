package com.example.go4lunch.model;

import androidx.annotation.Nullable;

import com.example.go4lunch.model.api.Photo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SelectedRestaurant implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("formatted_address")
    @Expose
    private String address;

    @SerializedName("rating")
    @Expose
    private double rating;

    @SerializedName("photos")
    @Expose
    @Nullable
    private List<Photo> photos;


    // Empty constructor to allow firebase to cast document to object model
    public SelectedRestaurant() {}

    public SelectedRestaurant(String id, String name, String address, double rating, List<Photo> photos) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.photos = photos;
    }


    // GETTERS
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }

    @Nullable
    public List<Photo> getPhotos() {
        return photos;
    }


    // SETTERS
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setPhotos(@Nullable List<Photo> photos) {
        this.photos = photos;
    }

}
