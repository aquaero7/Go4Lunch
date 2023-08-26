package com.example.go4lunch.model.model;

import androidx.annotation.Nullable;

import com.example.go4lunch.model.api.model.Geometry;
import com.example.go4lunch.model.api.model.OpeningHours;
import com.example.go4lunch.model.api.model.Photo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Restaurant implements Serializable {

    @SerializedName("place_id")
    @Expose
    private String rid;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("photos")
    @Expose
    @Nullable private List<Photo> photos;

    @SerializedName("formatted_address")
    @Expose
    private String address;

    @SerializedName("rating")
    @Expose
    private double rating;

    @SerializedName("opening_hours")
    @Expose
    @Nullable private OpeningHours openingHours;

    @SerializedName("formatted_phone_number")
    @Expose
    @Nullable private String phoneNumber;

    @SerializedName("website")
    @Expose
    @Nullable private String website;

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    // Empty constructor to allow firebase to cast document to object model
    public Restaurant() {}

    // Constructor
    public Restaurant(String rid, String name, @Nullable List<Photo> photos,
                      String address, double rating, @Nullable OpeningHours openingHours,
                      @Nullable String phoneNumber, @Nullable String website, Geometry geometry) {
        this.rid = rid;
        this.name = name;
        this.photos = photos;
        this.address = address;
        this.rating = rating;
        this.openingHours = openingHours;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.geometry = geometry;
    }


    // GETTERS

    public String getRid() {
        return rid;
    }
    public String getName() {
        return name;
    }
    @Nullable public List<Photo> getPhotos() {
        return photos;
    }
    public String getAddress() {
        return address;
    }
    public double getRating() {
        return rating;
    }
    @Nullable public OpeningHours getOpeningHours() {
        return openingHours;
    }
    @Nullable public String getPhoneNumber() {
        return phoneNumber;
    }
    @Nullable public String getWebsite() {
        return website;
    }
    public Geometry getGeometry() {
        return geometry;
    }


    // SETTERS

    public void setRid(String rid) {
        this.rid = rid;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPhotos(@Nullable List<Photo> photos) {
        this.photos = photos;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public void setOpeningHours(@Nullable OpeningHours openingHours) {
        this.openingHours = openingHours;
    }
    public void setPhoneNumber(@Nullable String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setWebsite(@Nullable String website) {
        this.website = website;
    }
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

}
