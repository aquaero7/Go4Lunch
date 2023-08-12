package com.example.go4lunch.model.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.model.User;
import com.example.go4lunch.model.helper.UserHelper;
import com.example.go4lunch.utils.Utils;
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
    private final MutableLiveData<List<User>> workmatesMutableLiveData;
    private final MutableLiveData<User> currentUserMutableLiveData;
    private final MutableLiveData<Boolean> userCreationResponseMutableLiveData;
    private final List<User> workmatesList;
    private final List<User> selectorsList;
    private User currentUser;

    private UserRepository() {
        userHelper = UserHelper.getInstance();

        workmatesMutableLiveData = new MutableLiveData<>();
        currentUserMutableLiveData = new MutableLiveData<>();

        userCreationResponseMutableLiveData = new MutableLiveData<>();

        workmatesList = new ArrayList<>();
        selectorsList = new ArrayList<>();
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

    /** For test use only : UserHelper dependency injection and new instance factory */
    private UserRepository(UserHelper userHelper) {
        this.userHelper = userHelper;

        workmatesMutableLiveData = new MutableLiveData<>();
        currentUserMutableLiveData = new MutableLiveData<>();

        userCreationResponseMutableLiveData = new MutableLiveData<>();

        workmatesList = new ArrayList<>();
        selectorsList = new ArrayList<>();
    }

    public static UserRepository getNewInstance(UserHelper userHelper) {
        instance = new UserRepository(userHelper);
        return instance;
    }
    /********************************************************************************/


    public CollectionReference getUsersCollection() {
        return userHelper.getUsersCollection();
    }

    public FirebaseUser getFbCurrentUser(){
        return userHelper.getFbCurrentUser();
    }

    public String getFbCurrentUserId() { return userHelper.getFbCurrentUserUID(); }

    public Boolean isFbCurrentUserLogged() {
        return (this.getFbCurrentUser() != null);
    }

    // Get the users list from Firestore
    public void getUsersList(OnCompleteListener<QuerySnapshot> listener) {
        userHelper.getUsersList(listener);
    }

    // Get the current user from Firestore and cast it to a User model Object
    public Task<User> getCurrentUserData() {
        return (userHelper.getCurrentUserData() != null) ?
                userHelper.getCurrentUserData().continueWith(task -> task.getResult().toObject(User.class)) : null;
    }

    // Create user in Firebase
    public void createUser() {
        userCreationResponseMutableLiveData.setValue(false);
        FirebaseUser cUser = getFbCurrentUser();
        if(cUser != null){
            // Data from FirebaseAuth
            final String USER_ID = "uid";
            String userUrlPicture = (cUser.getPhotoUrl() != null) ? cUser.getPhotoUrl().toString() : null;
            final String USER_NAME = "username";
            String username = cUser.getDisplayName();
            final String USER_EMAIL = "userEmail";
            String userEmail = cUser.getEmail();
            final String USER_URL_PICTURE = "userUrlPicture";
            String uid = cUser.getUid();

            // If the current user already exist in Firestore, we get his data from Firestore
            getCurrentUserData().addOnSuccessListener(user -> {
                if (user != null) {
                    // If the current user already exist in Firestore, we update his data
                    Log.w("UserRepository", "User already exists and will be updated");
                    getUsersCollection().document(uid)
                            .update(USER_ID, uid, USER_NAME, username, USER_EMAIL, userEmail,
                                    USER_URL_PICTURE, userUrlPicture)
                            .addOnSuccessListener(command -> {
                                Log.w("UserRepository","Update successful");
                                userCreationResponseMutableLiveData.setValue(true);
                                // Fetch current user
                                // fetchCurrentUser();
                            })
                            .addOnFailureListener(e -> Log.w("UserRepository",
                                    "Update failed. Message : " + e.getMessage()));
                } else {
                    // If the current user doesn't exist in Firestore, we create this user
                    Log.w("UserRepository", "User doesn't exist and will be created");
                    User userToCreate = new User(uid, username, userEmail, userUrlPicture);
                    getUsersCollection().document(uid)
                            .set(userToCreate)
                            .addOnSuccessListener(command -> {
                                Log.w("UserRepository","Creation successful");
                                userCreationResponseMutableLiveData.setValue(true);
                                // Fetch current user
                                // fetchCurrentUser();
                            })
                            .addOnFailureListener(e -> Log.w("UserRepository",
                                    "Creation failed. Message : " + e.getMessage()));
                }
            });
        }
    }

    // Set or update user selection restaurant id
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
                    sortByName(workmatesList);
                    // Populate the LiveData
                    workmatesMutableLiveData.setValue(workmatesList);
                }
            } else {
                Log.w("UserRepository", "Error getting documents: ", task.getException());
            }
        });
    }

    public void fetchCurrentUser() {
        // userCreationResponseMutableLiveData.setValue(false);
        getCurrentUserData()
                .addOnSuccessListener(user -> {
                    if (user != null) {
                        currentUser = user;
                        // Populate the LiveData
                        currentUserMutableLiveData.setValue(currentUser);
                        // userCreationResponseMutableLiveData.setValue(true);
                        Log.w("UserRepository", "breakPoint");
                    } else {
                        Log.w("UserRepository", "user is null");
                    }
                })
                .addOnFailureListener(e -> {
                    // userCreationResponseMutableLiveData.setValue(false);
                    Log.w("UserRepository", e.getMessage());
                });
    }

    public void sortByName(List<User> workmatesList) {
        workmatesList.sort(User.comparatorName);
    }

    public MutableLiveData<Boolean> getUserCreationResponseMutableLiveData() {
        return userCreationResponseMutableLiveData;
    }

    public MutableLiveData<List<User>> getWorkmatesMutableLiveData() {
        return workmatesMutableLiveData;
    }

    public MutableLiveData<User> getCurrentUserMutableLiveData() {
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
                sortByName(workmatesList);
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
                sortByName(workmatesList);
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

    public User getCurrentUser() {
        return currentUser;
    }
    public List<User> getWorkmates() {
        return workmatesList;
    }

    public void setSelectors(List<User> selectors) {
        selectorsList.clear();
        selectorsList.addAll(selectors);
    }

    public List<User> getSelectors() {
        return selectorsList;
    }

}
