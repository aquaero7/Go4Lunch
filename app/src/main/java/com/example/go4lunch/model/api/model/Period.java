package com.example.go4lunch.model.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Period implements Serializable {

    @SerializedName("close")
    @Expose
    private OpenClose close;

    @SerializedName("open")
    @Expose
    private OpenClose open;

    // Empty constructor to allow firebase to cast document to object model
    public Period() {
    }

    public Period(OpenClose close, OpenClose open) {
        this.close = close;
        this.open = open;
    }


    // GETTERS
    public OpenClose getClose() {
        return close;
    }
    public OpenClose getOpen() {
        return open;
    }

    // SETTERS
    public void setClose(OpenClose close) {
        this.close = close;
    }
    public void setOpen(OpenClose open) {
        this.open = open;
    }

}
