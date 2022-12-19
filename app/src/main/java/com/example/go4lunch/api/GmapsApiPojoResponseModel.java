package com.example.go4lunch.api;

import androidx.annotation.Nullable;

import com.example.go4lunch.model.Restaurant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GmapsApiPojoResponseModel {
    // POJO class to get the data from web api

    private List<Restaurant> restaurants;
    private String id;
    private String name;
    private int distance;
    @Nullable private String urlImage;
    @Nullable private String nationality;
    private String address;
    private int favRating;
    @Nullable private String openingTime;
    @Nullable private String phoneNumber;
    @Nullable private String website;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    // GETTERS

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

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

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Nullable
    public String getWebsite() {
        return website;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }


    // SETTERS

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

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

    public void setPhoneNumber(@Nullable String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setWebsite(@Nullable String website) {
        this.website = website;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
