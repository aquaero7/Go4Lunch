package com.example.go4lunch.manager;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.UserRepository;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserManager {

    private static volatile UserManager instance;
    private final UserRepository userRepository;

    private MutableLiveData<List<User>> workmatesMutableLiveData;
    private MutableLiveData<User> currentUserMutableLiveData;

    private List<User> workmatesList = new ArrayList<>();
    private User currentUser;

    private UserManager() {
        userRepository = UserRepository.getInstance();
        workmatesMutableLiveData = new MutableLiveData<>();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public CollectionReference getUsersCollection() {
        return userRepository.getUsersCollection();
    }

    public FirebaseUser getFbCurrentUser(){
        return userRepository.getFbCurrentUser();
    }

    public String getFbCurrentUserId() { return userRepository.getFbCurrentUserUID(); }

    public Boolean isFbCurrentUserLogged(){
        return (this.getFbCurrentUser() != null);
    }

    // Get the users list from Firestore
    public void getUsersList(OnCompleteListener<QuerySnapshot> listener) {
        userRepository.getUsersList(listener);
    }

    // Get the current user from Firestore and cast it to a User model Object
    public Task<User> getCurrentUserData() {
        return userRepository.getCurrentUserData().continueWith(task -> task.getResult().toObject(User.class)) ;
    }

    // Set ir update user selection restaurant id
    public Task<Void> updateSelectionId(String selectionId) {
        return userRepository.updateSelectionId(selectionId);
    }

    // Set or update user selection date
    public Task<Void> updateSelectionDate(String selectionDate) {
        return userRepository.updateSelectionDate(selectionDate);
    }

    // Set or update user selection name
    public Task<Void> updateSelectionName(String selectionName) {
        return userRepository.updateSelectionName(selectionName);
    }

    // Set or update user selection address
    public Task<Void> updateSelectionAddress(String selectionAddress) {
        return userRepository.updateSelectionAddress(selectionAddress);
    }

    // Set or update user search radius preferences
    public Task<Void> updateSearchRadiusPrefs(String searchRadiusPrefs) {
        return userRepository.updateSearchRadiusPrefs(searchRadiusPrefs);
    }

    // Set or update user notifications preferences
    public Task<Void> updateNotificationsPrefs(String notificationsPrefs) {
        return userRepository.updateNotificationsPrefs(notificationsPrefs);
    }

    public Task<Void> signOut(Context context){
        return userRepository.signOut(context);
    }

    public void deleteUser(String id) {
        userRepository.deleteUser(id);
    }

    public Task<Void> deleteFbUser(Context context){
        return userRepository.deleteFbUser(context);
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
                    currentUser = getCurrentUser();
                }
            } else {
                Log.w("UserManager", "Error getting documents: ", task.getException());
            }
        });
    }

    public List<User> getWorkmates() {
        return workmatesList;
    }

    // Update local list
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
            }
        }
    }

    /*
    public void fetchCurrentUser() {
        getCurrentUserData()
                .addOnSuccessListener(user -> {
                    currentUser = user;
                })
                .addOnFailureListener(e -> {
                    Log.w("UserManager", e.getMessage());
                });
    }
    */

    public User getCurrentUser() {
        for (User user : getWorkmates()) {
            if (Objects.equals(userRepository.getFbCurrentUserUID(), user.getUid())) {
                currentUser = user;
                break;
            }
        }
        return currentUser;
    }


}
