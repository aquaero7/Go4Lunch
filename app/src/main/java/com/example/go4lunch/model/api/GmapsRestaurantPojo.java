package com.example.go4lunch.model.api;

import com.example.go4lunch.model.model.Restaurant;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GmapsRestaurantPojo implements Serializable {
    // POJO class to get the data from web api

    @SerializedName("results")
    @Expose
    private List<Restaurant> nearRestaurants;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("error_message")
    @Expose
    private String errorMsg;

    @SerializedName("next_page_token")
    @Expose
    private String nextPageToken;


    public GmapsRestaurantPojo(List<Restaurant> nearRestaurants, String status, String errorMsg, String nextPageToken) {
        this.nearRestaurants = nearRestaurants;
        this.status = status;
        this.errorMsg = errorMsg;
        this.nextPageToken = nextPageToken;
    }


    // GETTERS

    public List<Restaurant> getNearRestaurants() {
        return nearRestaurants;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }


    // SETTERS

    public void setNearRestaurants(List<Restaurant> nearRestaurants) {
        this.nearRestaurants = nearRestaurants;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

}
