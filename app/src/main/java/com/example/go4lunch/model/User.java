package com.example.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {

    private String uid;
    private String username;
    @Nullable private String userEmail;
    @Nullable private String userUrlPicture;
    private List<String> likedRestaurantsIds;
    private String selectedRestaurantId;
    private String selectedRestaurantName;
    @Nullable private String selectionDate;


    // Empty constructor to allow firebase to cast document to object model
    public User() {}

    public User(String uid, String username, @Nullable String userEmail, @Nullable String userUrlPicture) {
        this.uid = uid;
        this.username = username;
        this.userEmail = userEmail;
        this.userUrlPicture = userUrlPicture;
        this.selectedRestaurantId = "";
        this.selectedRestaurantName = "";
        this.selectionDate = null;
        this.likedRestaurantsIds = new ArrayList<>();
    }

    //
    public User(String uid, String username, @Nullable String userEmail, @Nullable String userUrlPicture,
                String selectedRestaurantId, String selectedRestaurantName, @Nullable String selectionDate,
                List<String> likedRestaurantsIds) {
        this.uid = uid;
        this.username = username;
        this.userEmail = userEmail;
        this.userUrlPicture = userUrlPicture;
        this.selectedRestaurantId = selectedRestaurantId;
        this.selectedRestaurantName = selectedRestaurantName;
        this.selectionDate = selectionDate;
        this.likedRestaurantsIds = likedRestaurantsIds;
    }
    //

    // GETTERS
    public String getUid() {
        return uid;
    }
    public String getUsername() {
        return username;
    }
    @Nullable public String getUserEmail() {
        return userEmail;
    }
    @Nullable public String getUserUrlPicture() {
        return userUrlPicture;
    }
    public String getSelectedRestaurantId() {
        return selectedRestaurantId;
    }
    public String getSelectedRestaurantName() {
        return selectedRestaurantName;
    }
    @Nullable public String getSelectionDate() {
        return selectionDate;
    }
    public List<String> getLikedRestaurantsIds() {
        return likedRestaurantsIds;
    }


    // SETTERS
    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setUserEmail(@Nullable String userEmail) {
        this.userEmail = userEmail;
    }
    public void setUserUrlPicture(@Nullable String userUrlPicture) {
        this.userUrlPicture = userUrlPicture;
    }
    public void setSelectedRestaurantId(String selectedRestaurantId) {
        this.selectedRestaurantId = selectedRestaurantId;
    }
    public void setSelectedRestaurantName(String selectedRestaurantName) {
        this.selectedRestaurantName = selectedRestaurantName;
    }
    public void setSelectionDate(@Nullable String selectionDate) {
        this.selectionDate = selectionDate;
    }
    public void setLikedRestaurantsIds(List<String> likedRestaurantsIds) {
        this.likedRestaurantsIds = likedRestaurantsIds;
    }

}
