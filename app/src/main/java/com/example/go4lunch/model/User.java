package com.example.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.Comparator;
import java.util.List;

public class User {

    private String uid;
    private String username;
    @Nullable private String userEmail;
    @Nullable private String userUrlPicture;
    private String selectionId;
    @Nullable private String selectionDate;


    // Empty constructor to allow firebase to cast document to object model
    public User() {}

    public User(String uid, String username, @Nullable String userEmail, @Nullable String userUrlPicture) {
        this.uid = uid;
        this.username = username;
        this.userEmail = userEmail;
        this.userUrlPicture = userUrlPicture;
        this.selectionId = null;
        this.selectionDate = null;
    }

    public User(String uid, String username, @Nullable String userEmail, @Nullable String userUrlPicture,
                String selectionId, @Nullable String selectionDate) {
        this.uid = uid;
        this.username = username;
        this.userEmail = userEmail;
        this.userUrlPicture = userUrlPicture;
        this.selectionId = selectionId;
        this.selectionDate = selectionDate;
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
    public String getSelectionId() {
        return selectionId;
    }
    @Nullable public String getSelectionDate() {
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
    public void setSelectionId(String selectionId) {
        this.selectionId = selectionId;
    }
    public void setSelectionDate(@Nullable String selectionDate) {
        this.selectionDate = selectionDate;
    }

    // Comparator for sort by name
    public static Comparator<User> comparatorName = Comparator.comparing(User::getUsername);

}
