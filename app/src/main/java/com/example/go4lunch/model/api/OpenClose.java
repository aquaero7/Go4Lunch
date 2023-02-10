package com.example.go4lunch.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OpenClose implements Serializable {

    @SerializedName("day")
    @Expose
    private int day;

    @SerializedName("time")
    @Expose
    private String time;

    // Empty constructor to allow firebase to cast document to object model
    public OpenClose() {
    }


    // GETTERS
    public int getDay() {
        return day;
    }
    public String getTime() {
        return time;
    }

    // SETTERS
    public void setDay(int day) {
        this.day = day;
    }
    public void setTime(String time) {
        this.time = time;
    }

}
