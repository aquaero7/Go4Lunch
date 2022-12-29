package com.example.go4lunch.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OpeningHours implements Serializable {

    @SerializedName("open_now")
    @Expose
    private boolean openNow;

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

}
