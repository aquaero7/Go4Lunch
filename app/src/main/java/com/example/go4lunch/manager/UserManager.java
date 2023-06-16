package com.example.go4lunch.manager;

import android.content.Context;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;

public class UserManager {

    private static volatile UserManager instance;
    private final UserRepository userRepository;
    private UserManager() {
        userRepository = UserRepository.getInstance();
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

    public FirebaseUser getCurrentUser(){
        return userRepository.getCurrentUser();
    }

    public String getCurrentUserId() { return userRepository.getCurrentUserUID(); }

    public Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }

    /*
    public void createUser(){
        userRepository.createUser();
    }
    */

    // Get the users list from Firestore
    public void getUsersList(OnCompleteListener<QuerySnapshot> listener) {
        userRepository.getUsersList(listener);
    }

    // Get the current user from Firestore and cast it to a User model Object
    public Task<User> getCurrentUserData() {
        return userRepository.getCurrentUserData().continueWith(task -> task.getResult().toObject(User.class)) ;
    }

    /* Get the user from Firestore and cast it to a User model Object
    public Task<User> getUserData(String id) {
        return userRepository.getUserData(id).continueWith(task -> task.getResult().toObject(User.class)) ;
    }
    */

    // Set ir update user selection restaurant id
    public Task<Void> updateSelectionId(String selectionId) {
        return userRepository.updateSelectionId(selectionId);
    }

    // Set or update user selection date
    public Task<Void> updateSelectionDate(String selectionDate) {
        return userRepository.updateSelectionDate(selectionDate);
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

    public Task<Void> deleteFirebaseUser(Context context){
        return userRepository.deleteFirebaseUser(context);
    }

}
