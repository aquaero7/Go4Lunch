package com.example.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {

    private String id;
    private String name;
    private int distance;
    @Nullable private String urlImage;
    @Nullable private String nationality;
    private String address;
    private int favRating;
    @Nullable private String openingTime;
    private int likesCount;
    @Nullable private String phoneNumber;
    @Nullable private String website;
    private List<String> selectors;

    /*
    public Restaurant(String name, int distance, @Nullable String urlImage,
                      @Nullable String nationality, String address, int favRating,
                      @Nullable String openingTime, int likesCount, @Nullable String phoneNumber,
                      @Nullable String website) {
        this.name = name;
        this.distance = distance;
        this.urlImage = urlImage;
        this.nationality = nationality;
        this.address = address;
        this.favRating = favRating;
        this.openingTime = openingTime;
        this.likesCount = likesCount;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.selectors = new ArrayList<>();
    }
    */

    public Restaurant(String id, String name, int distance, @Nullable String urlImage,
                      @Nullable String nationality, String address, int favRating,
                      @Nullable String openingTime, int likesCount, @Nullable String phoneNumber,
                      @Nullable String website) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.urlImage = urlImage;
        this.nationality = nationality;
        this.address = address;
        this.favRating = favRating;
        this.openingTime = openingTime;
        this.likesCount = likesCount;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.selectors = new ArrayList<>();
    }

    /*
    public Restaurant(String id, String name, int distance, @Nullable String urlImage,
                      @Nullable String nationality, String address, int favRating,
                      @Nullable String openingTime, int likesCount, @Nullable String phoneNumber,
                      @Nullable String website, List<String> selectors) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.urlImage = urlImage;
        this.nationality = nationality;
        this.address = address;
        this.favRating = favRating;
        this.openingTime = openingTime;
        this.likesCount = likesCount;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.selectors = selectors;
    }
    */

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
    public String getUrlImage() {
        return urlImage;
    }
    @Nullable
    public String getNationality() {
        return nationality;
    }
    public String getAddress() {
        return address;
    }
    public int getFavRating() {
        return favRating;
    }
    @Nullable
    public String getOpeningTime() {
        return openingTime;
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
    public List<String> getSelectors() {
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
    public void setUrlImage(@Nullable String urlImage) {
        this.urlImage = urlImage;
    }
    public void setNationality(@Nullable String nationality) {
        this.nationality = nationality;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setFavRating(int favRating) {
        this.favRating = favRating;
    }
    public void setOpeningTime(@Nullable String openingTime) {
        this.openingTime = openingTime;
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
    public void setSelectors(List<String> selectors) {
        this.selectors = selectors;
    }

}
