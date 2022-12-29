package com.example.go4lunch.model;

import androidx.annotation.Nullable;

import com.example.go4lunch.model.api.OpeningHours;
import com.example.go4lunch.model.api.Photo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Restaurant implements Serializable {

    @SerializedName("place_id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    private int distance;

    @SerializedName("photos")
    @Expose
    @Nullable private List<Photo> photos;

    @Nullable private String imageUrl;

    @Nullable private String nationality;

    @SerializedName("formatted_address")
    @Expose
    private String address;

    @SerializedName("rating")
    @Expose
    private double rating;

    @SerializedName("opening_hours")
    @Expose
    @Nullable private OpeningHours openingHours;

    private int likesCount;

    @SerializedName("formatted_phone_number")
    @Expose
    @Nullable private String phoneNumber;

    @SerializedName("website")
    @Expose
    @Nullable private String website;

    @Nullable private List<User> selectors;

    // Database constructor (full attributes)
    public Restaurant(String id, String name, int distance, @Nullable String imageUrl,
                      @Nullable String nationality, String address, double rating,
                      @Nullable OpeningHours openingHours, int likesCount, @Nullable String phoneNumber,
                      @Nullable String website, @Nullable List<User> selectors) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.imageUrl = imageUrl;
        this.nationality = nationality;
        this.address = address;
        this.rating = rating;
        this.openingHours = openingHours;
        this.likesCount = likesCount;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.selectors = selectors;
    }

    // API constructor
    public Restaurant(String id, String name, int distance, @Nullable String imageUrl,
                      @Nullable String nationality, String address, double rating,
                      @Nullable OpeningHours openingHours, @Nullable String phoneNumber,
                      @Nullable String website) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.imageUrl = imageUrl;
        this.nationality = nationality;
        this.address = address;
        this.rating = rating;
        this.openingHours = openingHours;
        this.phoneNumber = phoneNumber;
        this.website = website;
    }


    // GETTERS

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getDistance() {
        return distance;
    }
    @Nullable
    public List<Photo> getPhotos() {
        return photos;
    }
    @Nullable
    public String getImageUrl() {
        // return imageUrl;
        if (getPhotos() != null) {
            return getPhotos().get(0).getPhotoUrl();
        } else return "";
    }
    @Nullable
    public String getNationality() {
        return nationality;
    }
    public String getAddress() {
        return address;
    }
    public double getRating() {
        return rating;
    }
    @Nullable
    public OpeningHours getOpeningHours() {
        return openingHours;
    }
    public int getLikesCount() {
        return likesCount;
    }
    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }
    @Nullable
    public String getWebsite() {
        return website;
    }
    public List<User> getSelectors() {
        return selectors;
    }


    // SETTERS
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public void setPhotos(@Nullable List<Photo> photos) {
        this.photos = photos;
    }
    public void setImageUrl(@Nullable String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setNationality(@Nullable String nationality) {
        this.nationality = nationality;
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
    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
    public void setPhoneNumber(@Nullable String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setWebsite(@Nullable String website) {
        this.website = website;
    }
    public void setSelectors(@Nullable List<User> selectors) {
        this.selectors = selectors;
    }

}
