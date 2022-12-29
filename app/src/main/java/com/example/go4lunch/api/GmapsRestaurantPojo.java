package com.example.go4lunch.api;

import androidx.annotation.Nullable;

import com.example.go4lunch.model.Restaurant;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
