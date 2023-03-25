package com.example.go4lunch.model;

import androidx.annotation.Nullable;

import com.example.go4lunch.model.api.Geometry;
import com.example.go4lunch.model.api.OpeningHours;
import com.example.go4lunch.model.api.Photo;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class RestaurantWithDistance implements Serializable {

    private String rid;
    private String name;
    @Nullable private List<Photo> photos;
    private String address;
    private double rating;
    @Nullable private OpeningHours openingHours;
    @Nullable private String phoneNumber;
    @Nullable private String website;
    private Geometry geometry;
    private long distance;

    // Constructor
    public RestaurantWithDistance(String rid, String name, @Nullable List<Photo> photos,
                                  String address, double rating, @Nullable OpeningHours openingHours,
                                  @Nullable String phoneNumber, @Nullable String website, Geometry geometry, long distance) {
        this.rid = rid;
        this.name = name;
        this.photos = photos;
        this.address = address;
        this.rating = rating;
        this.openingHours = openingHours;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.geometry = geometry;
        this.distance = distance;
    }


    // GETTERS
    public String getRid() {
        return rid;
    }
    public String getName() {
        return name;
    }
    @Nullable public List<Photo> getPhotos() {
        return photos;
    }
    public String getAddress() {
        return address;
    }
    public double getRating() {
        return rating;
    }
    @Nullable public OpeningHours getOpeningHours() {
        return openingHours;
    }
    @Nullable public String getPhoneNumber() {
        return phoneNumber;
    }
    @Nullable public String getWebsite() {
        return website;
    }
    public Geometry getGeometry() {
        return geometry;
    }
    public long getDistance() {
        return distance;
    }


    // SETTERS
    public void setRid(String rid) {
        this.rid = rid;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPhotos(@Nullable List<Photo> photos) {
        this.photos = photos;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public void setOpeningHours(@Nullable OpeningHours openingHours) {
        this.openingHours = openingHours;
    }
    public void setPhoneNumber(@Nullable String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setWebsite(@Nullable String website) {
        this.website = website;
    }
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
    public void setDistance(long distance) {
        this.distance = distance;
    }


    // SORTS

    // Comparator for sort by distance
    public static Comparator<RestaurantWithDistance> comparatorDistance = (o1, o2) -> (int) (o1.getDistance() - o2.getDistance());

    // Comparator for sort by name
    public static Comparator<RestaurantWithDistance> comparatorName = Comparator.comparing(RestaurantWithDistance::getName);

}
