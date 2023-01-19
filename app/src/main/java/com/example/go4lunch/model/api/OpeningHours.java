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


    // Class Period
    public class Period implements Serializable {

        @SerializedName("close")
        @Expose
        private OpenClose close;

        @SerializedName("open")
        @Expose
        private OpenClose open;

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


    // Class OpenClose
    public class OpenClose implements Serializable {

        @SerializedName("day")
        @Expose
        private int day;

        @SerializedName("time")
        @Expose
        private String time;

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

}
