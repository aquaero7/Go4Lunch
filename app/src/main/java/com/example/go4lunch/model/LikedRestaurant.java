package com.example.go4lunch.model;

public class LikedRestaurant {

    private String id;
    private String rid;
    private String uid;

    // Empty constructor to allow firebase to cast document to object model
    public LikedRestaurant() {
    }

    // Constructor used to create and read liked restaurant from database
    public LikedRestaurant(String id, String rid, String uid) {
        this.id = id;
        this.rid = rid;
        this.uid = uid;
    }

    // GETTERS
    public String getId() {
        return id;
    }
    public String getRid() {
        return rid;
    }
    public String getUid() {
        return uid;
    }

    // SETTERS
    public void setId(String id) {
        this.id = id;
    }
    public void setRid(String rid) {
        this.rid = rid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    
}
