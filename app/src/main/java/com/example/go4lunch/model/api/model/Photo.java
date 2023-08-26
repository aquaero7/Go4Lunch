package com.example.go4lunch.model.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Photo implements Serializable {

    @SerializedName("photo_reference")
    @Expose
    private String photoReference;

    @SerializedName("html_attributions")
    @Expose
    private List<String> htmlAttributions = null;

    @SerializedName("height")
    @Expose
    private long height;

    @SerializedName("width")
    @Expose
    private long width;

    // Empty constructor to allow firebase to cast document to object model
    public Photo() {}

    // Constructor
    public Photo(String photoReference, List<String> htmlAttributions, long height, long width) {
        this.photoReference = photoReference;
        this.htmlAttributions = htmlAttributions;
        this.height = height;
        this.width = width;
    }


    // GETTERS

    public String getPhotoReference() {
        return photoReference;
    }

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public long getHeight() {
        return height;
    }

    public long getWidth() {
        return width;
    }

    public String getPhotoUrl(String key) {
        String rootUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=";
        return rootUrl + photoReference + "&key=" + key;
    }

    public static String getPhotoUrl(String photoReference, String key) {
        String rootUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=";
        return rootUrl + photoReference + "&key=" + key;
    }


    // SETTERS

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public void setHtmlAttributions(List<String> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public void setWidth(long width) {
        this.width = width;
    }

}
