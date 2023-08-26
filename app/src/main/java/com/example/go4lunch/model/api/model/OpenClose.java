package com.example.go4lunch.model.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OpenClose implements Serializable {

    @SerializedName("day")
    @Expose
    private long day;

    @SerializedName("time")
    @Expose
    private String time;

    // Empty constructor to allow firebase to cast document to object model
    public OpenClose() {
    }

    // Constructor
    public OpenClose(long day, String time) {
        this.day = day;
        this.time = time;
    }


    // GETTERS
    public long getDay() {
        return day;
    }
    public String getTime() {
        return time;
    }

    // SETTERS
    public void setDay(long day) {
        this.day = day;
    }
    public void setTime(String time) {
        this.time = time;
    }

}
