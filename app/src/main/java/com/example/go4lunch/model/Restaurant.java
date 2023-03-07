package com.example.go4lunch.model;

import androidx.annotation.Nullable;

import com.example.go4lunch.R;
import com.example.go4lunch.model.api.Geometry;
import com.example.go4lunch.model.api.OpeningHours;
import com.example.go4lunch.model.api.Photo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class Restaurant implements Serializable {

    @SerializedName("place_id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    private long distance;

    @SerializedName("photos")
    @Expose
    @Nullable private List<Photo> photos;

    @Nullable private String nationality;

    @SerializedName("formatted_address")
    @Expose
    private String address;

    @SerializedName("rating")
    @Expose
    private double rating;

    @SerializedName("opening_hours")
    @Expose
    @Nullable private OpeningHours openingHours;

    private String openingInformation;

    private int likesCount;

    @SerializedName("formatted_phone_number")
    @Expose
    @Nullable private String phoneNumber;

    @SerializedName("website")
    @Expose
    @Nullable private String website;

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    @Nullable private List<User> selectors;


    // Empty constructor to allow firebase to cast document to object model
    public Restaurant() {}

    // Database constructor (full attributes)
    public Restaurant(String id, String name, long distance, @Nullable List<Photo> photos,
                      @Nullable String nationality, String address, double rating,
                      @Nullable OpeningHours openingHours, int likesCount, @Nullable String phoneNumber,
                      @Nullable String website, Geometry geometry, @Nullable List<User> selectors) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.photos = photos;
        this.nationality = nationality;
        this.address = address;
        this.rating = rating;
        this.openingHours = openingHours;
        this.likesCount = likesCount;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.geometry = geometry;
        this.selectors = selectors;
    }

    // API constructor
    public Restaurant(String id, String name, long distance, @Nullable List<Photo> photos,
                      @Nullable String nationality, String address, double rating,
                      @Nullable OpeningHours openingHours, @Nullable String phoneNumber,
                      @Nullable String website, Geometry geometry) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.photos = photos;
        this.nationality = nationality;
        this.address = address;
        this.rating = rating;
        this.openingHours = openingHours;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.geometry = geometry;
    }

    // Display constructor (full attributes)
    public Restaurant(String id, String name, long distance, @Nullable List<Photo> photos,
                      @Nullable String nationality, String address, double rating,
                      String openingInformation, int likesCount, @Nullable String phoneNumber,
                      @Nullable String website, Geometry geometry, @Nullable List<User> selectors) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.photos = photos;
        this.nationality = nationality;
        this.address = address;
        this.rating = rating;
        this.openingInformation = openingInformation;
        this.likesCount = likesCount;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.geometry = geometry;
        this.selectors = selectors;
    }


    // GETTERS

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public long getDistance() {
        return distance;
    }
    @Nullable
    public List<Photo> getPhotos() {
        return photos;
    }
    @Nullable
    public String getNationality() {
        return nationality;
    }
    public String getAddress() {
        return address;
    }
    public double getRating() {
        return rating;
    }
    @Nullable
    public OpeningHours getOpeningHours() {
        return openingHours;
    }
    public String getOpeningInformation() {
        return openingInformation;
    }
    public int getLikesCount() {
        return likesCount;
    }
    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }
    @Nullable
    public String getWebsite() {
        return website;
    }
    public Geometry getGeometry() {
        return geometry;
    }
    public List<User> getSelectors() {
        return selectors;
    }


    // SETTERS
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDistance(long distance) {
        this.distance = distance;
    }
    public void setPhotos(@Nullable List<Photo> photos) {
        this.photos = photos;
    }
    public void setNationality(@Nullable String nationality) {
        this.nationality = nationality;
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
    public void setOpeningInformation(String openingInformation) {
        this.openingInformation = openingInformation;
    }
    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
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
    public void setSelectors(@Nullable List<User> selectors) {
        this.selectors = selectors;
    }


    // SORTS

    // Comparator for sort by distance
    public static Comparator<Restaurant> comparatorDistance = (o1, o2) -> (int) (o1.getDistance() - o2.getDistance());

    // Comparator for sort by name
    public static Comparator<Restaurant> comparatorName = Comparator.comparing(Restaurant::getName);



}
