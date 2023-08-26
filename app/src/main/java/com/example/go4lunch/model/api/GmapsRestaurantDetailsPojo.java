package com.example.go4lunch.model.api;

import com.example.go4lunch.model.model.Restaurant;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * POJO class to get the data from web api
 */
public class GmapsRestaurantDetailsPojo implements Serializable {
    @SerializedName("result")
    @Expose
    private Restaurant restaurantDetails;

    @SerializedName("status")
    @Expose
    private String status;

    // Constructor
    public GmapsRestaurantDetailsPojo(Restaurant restaurantDetails, String status) {
        this.restaurantDetails = restaurantDetails;
        this.status = status;
    }


    // GETTERS

    public Restaurant getRestaurantDetails() {
        return restaurantDetails;
    }

    public String getStatus() {
        return status;
    }


    // SETTERS

    public void setRestaurantDetails(Restaurant restaurantDetails) {
        this.restaurantDetails = restaurantDetails;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
