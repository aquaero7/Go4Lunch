package com.example.go4lunch.model.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.model.User;
import com.example.go4lunch.model.helper.UserHelper;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserRepository {

    private static volatile UserRepository instance;
    private final UserHelper userHelper;

    private MutableLiveData<List<User>> workmatesMutableLiveData;
    private MutableLiveData<User> currentUserMutableLiveData;

    private List<User> workmatesList = new ArrayList<>();
    private User currentUser;
    private boolean locationPermissionsGranted;

    private UserRepository() {
        userHelper = UserHelper.getInstance();
        workmatesMutableLiveData = new MutableLiveData<>();
        currentUserMutableLiveData = new MutableLiveData<>();
    }

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserHelper.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    public CollectionReference getUsersCollection() {
        return userHelper.getUsersCollection();
    }

    public FirebaseUser getFbCurrentUser(){
        return userHelper.getFbCurrentUser();
    }

    public String getFbCurrentUserId() { return userHelper.getFbCurrentUserUID(); }

    public Boolean isFbCurrentUserLogged(){
        return (this.getFbCurrentUser() != null);
    }

    // Get the users list from Firestore
    public void getUsersList(OnCompleteListener<QuerySnapshot> listener) {
        userHelper.getUsersList(listener);
    }

    // Get the current user from Firestore and cast it to a User model Object
    public Task<User> getCurrentUserData() {
        return userHelper.getCurrentUserData().continueWith(task -> task.getResult().toObject(User.class)) ;
    }

    // Set ir update user selection restaurant id
    public Task<Void> updateSelectionId(String selectionId) {
        return userHelper.updateSelectionId(selectionId);
    }

    // Set or update user selection date
    public Task<Void> updateSelectionDate(String selectionDate) {
        return userHelper.updateSelectionDate(selectionDate);
    }

    // Set or update user selection name
    public Task<Void> updateSelectionName(String selectionName) {
        return userHelper.updateSelectionName(selectionName);
    }

    // Set or update user selection address
    public Task<Void> updateSelectionAddress(String selectionAddress) {
        return userHelper.updateSelectionAddress(selectionAddress);
    }

    // Set or update user search radius preferences
    public Task<Void> updateSearchRadiusPrefs(String searchRadiusPrefs) {
        return userHelper.updateSearchRadiusPrefs(searchRadiusPrefs);
    }

    // Set or update user notifications preferences
    public Task<Void> updateNotificationsPrefs(String notificationsPrefs) {
        return userHelper.updateNotificationsPrefs(notificationsPrefs);
    }

    // Method using AuthUI
    public Task<Void> signOut(Context context){
        return userHelper.signOut(context);
    }

    // Method using FirebaseAuth
    public void signOut(){
        userHelper.signOut();
    }

    public void deleteUser(String id) {
        userHelper.deleteUser(id);
    }

    // Method using AuthUI
    public Task<Void> deleteFbUser(Context context){
        return userHelper.deleteFbUser(context);
    }

    // Method using FirebaseAuth
    public Task<Void> deleteFbUser(){
        return userHelper.deleteFbUser();
    }

    public void fetchWorkmates() {
        // Get workmates list from database document
        getUsersList(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    // Get users list
                    if (workmatesList != null) workmatesList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Map<String, Object> userData = document.getData(); // Map data for debug.
                        String uId = Objects.requireNonNull(document.getData().get("uid")).toString();
                        String uName = Objects.requireNonNull(document.getData().get("username")).toString();
                        String uEmail = ((document.getData().get("userEmail")) != null) ? document.getData().get("userEmail").toString() : null;
                        String uUrlPicture = ((document.getData().get("userUrlPicture")) != null) ? document.getData().get("userUrlPicture").toString() : null;
                        String selectionId = ((document.getData().get("selectionId")) != null) ? document.getData().get("selectionId").toString() : null;
                        String selectionDate = ((document.getData().get("selectionDate")) != null) ? document.getData().get("selectionDate").toString() : null;
                        String selectionName = ((document.getData().get("selectionName")) != null) ? document.getData().get("selectionName").toString() : null;
                        String selectionAddress = ((document.getData().get("selectionAddress")) != null) ? document.getData().get("selectionAddress").toString() : null;
                        String searchRadiusPrefs = ((document.getData().get("searchRadiusPrefs")) != null) ? document.getData().get("searchRadiusPrefs").toString() : null;
                        String notificationsPrefs = ((document.getData().get("notificationsPrefs")) != null) ? document.getData().get("notificationsPrefs").toString() : null;

                        User workmateToAdd = new User(uId, uName, uEmail, uUrlPicture, selectionId, selectionDate,
                                selectionName, selectionAddress, searchRadiusPrefs, notificationsPrefs);
                        workmatesList.add(workmateToAdd);
                    }
                    DataProcessingUtils.sortByName(workmatesList);
                    // Populate the LiveData
                    workmatesMutableLiveData.setValue(workmatesList);
                }
            } else {
                Log.w("UserRepository", "Error getting documents: ", task.getException());
            }
        });
    }

    public void fetchCurrentUser() {
        getCurrentUserData()
                .addOnSuccessListener(user -> {
                    currentUser = user;
                    // Populate the LiveData
                    currentUserMutableLiveData.setValue(currentUser);
                    Log.w("UserRepository", "breakPoint");
                })
                .addOnFailureListener(e -> {
                    Log.w("UserRepository", e.getMessage());
                });
    }

    public MutableLiveData<List<User>> getWorkmatesMutableLiveData() {
        // Populate the LiveData
        // workmatesMutableLiveData.setValue(workmatesList);
        return workmatesMutableLiveData;
    }

    public MutableLiveData<User> getCurrentUserMutableLiveData() {
        // Populate the LiveData
        // currentUserMutableLiveData.setValue(currentUser);
        return currentUserMutableLiveData;
    }

    // Update workmates list
    public void updateWorkmates(String rId, String currentDate, String rName, String rAddress) {
        for (User workmate : workmatesList) {
            if (Objects.equals(getFbCurrentUserId(), workmate.getUid())) {
                // Remove workmate from the list
                workmatesList.remove(workmate);
                // Update workmate
                workmate.setSelectionId(rId);
                workmate.setSelectionDate(currentDate);
                workmate.setSelectionName(rName);
                workmate.setSelectionAddress(rAddress);
                // Add updated workmate to list
                workmatesList.add(workmate);
                // Sort list
                DataProcessingUtils.sortByName(workmatesList);
                // Populate the LiveData
                workmatesMutableLiveData.setValue(workmatesList);
                break;
            }
        }
    }

    public void updateWorkmates(String prefType, String prefValue) {
        for (User workmate : workmatesList) {
            if (Objects.equals(getFbCurrentUserId(), workmate.getUid())) {
                // Remove workmate from the list
                workmatesList.remove(workmate);
                // Update prefs
                switch (prefType) {
                    // Update search radius prefs
                    case "RAD" :
                        workmate.setSearchRadiusPrefs(prefValue);
                        break;
                    // Update notifications prefs
                    case "NOT" :
                        workmate.setNotificationsPrefs(prefValue);
                        break;
                }
                // Add updated workmate to list
                workmatesList.add(workmate);
                // Sort list
                DataProcessingUtils.sortByName(workmatesList);
                // Populate the LiveData
                workmatesMutableLiveData.setValue(workmatesList);
                break;
            }
        }
    }

    public void updateCurrentUser(String rId, String currentDate, String rName, String rAddress) {
        currentUser.setSelectionId(rId);
        currentUser.setSelectionDate(currentDate);
        currentUser.setSelectionName(rName);
        currentUser.setSelectionAddress(rAddress);
        // Populate the LiveData
        currentUserMutableLiveData.setValue(currentUser);
    }

    public void updateCurrentUser(String prefType, String prefValue) {
        // Update preferences
        switch (prefType) {
            // Update search radius prefs
            case "RAD" :
                currentUser.setSearchRadiusPrefs(prefValue);
                break;
            // Update notifications prefs
            case "NOT" :
                currentUser.setNotificationsPrefs(prefValue);
                break;
        }
        // Populate the LiveData
        currentUserMutableLiveData.setValue(currentUser);
    }

    public void setPermissions(boolean granted) {
        locationPermissionsGranted = granted;
    }

    public boolean arePermissionsGranted() {
        return locationPermissionsGranted;
    }


    // For notification service and detail restaurant activity
    public User getCurrentUser() {
        return currentUser;
    }
    public List<User> getWorkmates() {
        return workmatesList;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    public MutableLiveData<List<User>> getWorkmatesMutableLiveData() {
        workmatesMutableLiveData = new MutableLiveData<>();

        // Get workmates list from database document
        getUsersList(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    // Get users list
                    if (workmatesList != null) workmatesList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Map<String, Object> userData = document.getData(); // Map data for debug.
                        String uId = Objects.requireNonNull(document.getData().get("uid")).toString();
                        String uName = Objects.requireNonNull(document.getData().get("username")).toString();
                        String uEmail = ((document.getData().get("userEmail")) != null) ? document.getData().get("userEmail").toString() : null;
                        String uUrlPicture = ((document.getData().get("userUrlPicture")) != null) ? document.getData().get("userUrlPicture").toString() : null;
                        String selectionId = ((document.getData().get("selectionId")) != null) ? document.getData().get("selectionId").toString() : null;
                        String selectionDate = ((document.getData().get("selectionDate")) != null) ? document.getData().get("selectionDate").toString() : null;
                        String selectionName = ((document.getData().get("selectionName")) != null) ? document.getData().get("selectionName").toString() : null;
                        String selectionAddress = ((document.getData().get("selectionAddress")) != null) ? document.getData().get("selectionAddress").toString() : null;
                        String searchRadiusPrefs = ((document.getData().get("searchRadiusPrefs")) != null) ? document.getData().get("searchRadiusPrefs").toString() : null;
                        String notificationsPrefs = ((document.getData().get("notificationsPrefs")) != null) ? document.getData().get("notificationsPrefs").toString() : null;

                        User workmateToAdd = new User(uId, uName, uEmail, uUrlPicture, selectionId, selectionDate,
                                selectionName, selectionAddress, searchRadiusPrefs, notificationsPrefs);
                        workmatesList.add(workmateToAdd);
                    }
                    DataProcessingUtils.sortByName(workmatesList);
                    // Populate the LiveData
                    workmatesMutableLiveData.setValue(workmatesList);
                }
            } else {
                Log.w("UserRepository", "Error getting documents: ", task.getException());
            }
        });
        return workmatesMutableLiveData;
    }
    */

    /*
    public MutableLiveData<User> getCurrentUserMutableLiveData() {
        currentUserMutableLiveData = new MutableLiveData<>();

        // Get current user from Firebase
        getCurrentUserData()
                .addOnSuccessListener(user -> {
                    // Populate the LiveData
                    currentUserMutableLiveData.setValue(user);
                })
                .addOnFailureListener(e -> {
                    Log.w("UserRepository", e.getMessage());
                });
        return currentUserMutableLiveData;
    }
    */

}
