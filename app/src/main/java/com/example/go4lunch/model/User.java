package com.example.go4lunch.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Comparator;

public class User implements Serializable {

    private String uid;
    private String username;
    @Nullable private String userEmail;
    @Nullable private String userUrlPicture;
    @Nullable private String selectionId;
    @Nullable private String selectionDate;
    @Nullable private String searchRadiusPrefs;
    @Nullable private String notificationsPrefs;


    // Empty constructor to allow firebase to cast document to object model
    public User() {}

    // API constructor
    public User(String uid, String username, @Nullable String userEmail, @Nullable String userUrlPicture) {
        this.uid = uid;
        this.username = username;
        this.userEmail = userEmail;
        this.userUrlPicture = userUrlPicture;
        this.selectionId = null;
        this.selectionDate = null;
        this.searchRadiusPrefs = null;
        this.notificationsPrefs = null;
    }

    // Full constructor
    public User(String uid, String username, @Nullable String userEmail, @Nullable String userUrlPicture,
                @Nullable String selectionId, @Nullable String selectionDate,
                @Nullable String searchRadiusPrefs, @Nullable String notificationsPrefs) {
        this.uid = uid;
        this.username = username;
        this.userEmail = userEmail;
        this.userUrlPicture = userUrlPicture;
        this.selectionId = selectionId;
        this.selectionDate = selectionDate;
        this.searchRadiusPrefs = searchRadiusPrefs;
        this.notificationsPrefs = notificationsPrefs;
    }


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
    @Nullable public String getSelectionId() {
        return selectionId;
    }
    @Nullable public String getSelectionDate() {
        return selectionDate;
    }
    @Nullable public String getSearchRadiusPrefs() {
        return searchRadiusPrefs;
    }
    @Nullable public String getNotificationsPrefs() {
        return notificationsPrefs;
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
    public void setSelectionId(@Nullable String selectionId) {
        this.selectionId = selectionId;
    }
    public void setSelectionDate(@Nullable String selectionDate) {
        this.selectionDate = selectionDate;
    }
    public void setSearchRadiusPrefs(@Nullable String searchRadiusPrefs) {
        this.searchRadiusPrefs = searchRadiusPrefs;
    }
    public void setNotificationsPrefs(@Nullable String notificationsPrefs) {
        this.notificationsPrefs = notificationsPrefs;
    }


    // SORTS

    // Comparator for sort by name
    public static Comparator<User> comparatorName = Comparator.comparing(User::getUsername);

}
