package com.example.go4lunch.models;

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
    @Nullable private Date selectionDate;


    public User(String uid, String username, @Nullable String userEmail, @Nullable String userUrlPicture) {
        this.uid = uid;
        this.username = username;
        this.userEmail = userEmail;
        this.userUrlPicture = userUrlPicture;
        this.likedRestaurantsIds = new ArrayList<>();
        this.selectedRestaurantId = "";
        this.selectionDate = null;
    }

    /*
    public User(String uid, String username, @Nullable String userEmail, @Nullable String userUrlPicture,
                List<String> likedRestaurantsIds, String selectedRestaurantId,
                @Nullable Date selectionDate) {
        this.uid = uid;
        this.username = username;
        this.userEmail = userEmail;
        this.userUrlPicture = userUrlPicture;
        this.likedRestaurantsIds = likedRestaurantsIds;
        this.selectedRestaurantId = selectedRestaurantId;
        this.selectionDate = selectionDate;
    }
    */

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
    public List<String> getLikedRestaurantsIds() {
        return likedRestaurantsIds;
    }
    public String getSelectedRestaurantId() {
        return selectedRestaurantId;
    }
    @Nullable public Date getSelectionDate() {
        return selectionDate;
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
    public void setLikedRestaurantsIds(List<String> likedRestaurantsIds) {
        this.likedRestaurantsIds = likedRestaurantsIds;
    }
    public void setSelectedRestaurantId(String selectedRestaurantId) {
        this.selectedRestaurantId = selectedRestaurantId;
    }
    public void setSelectionDate(@Nullable Date selectionDate) {
        this.selectionDate = selectionDate;
    }

}
