package com.example.go4lunch.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OpeningHours implements Serializable {

    @SerializedName("open_now")
    @Expose
    private boolean openNow;

    @SerializedName("periods")
    @Expose
    private List<Period> periods;

    @SerializedName("weekday_text")
    @Expose
    private List<String> weekdayText;

    // Empty constructor to allow firebase to cast document to object model
    public OpeningHours() {

    }


    // GETTERS
    public boolean isOpenNow() {
        return openNow;
    }
    public List<Period> getPeriods() {
        return periods;
    }
    public List<String> getWeekdayText() {
        return weekdayText;
    }

    // SETTERS
    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }
    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }
    public void setWeekdayText(List<String> weekdayText) {
        this.weekdayText = weekdayText;
    }

}
