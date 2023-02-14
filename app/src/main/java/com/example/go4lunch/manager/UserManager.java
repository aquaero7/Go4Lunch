package com.example.go4lunch.manager;

import android.content.Context;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
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

    public FirebaseUser getCurrentUser(){
        return userRepository.getCurrentUser();
    }

    public String getCurrentUserId() { return userRepository.getCurrentUserUID(); }

    public Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context){
        return userRepository.signOut(context);
    }

    public void createUser(){
        userRepository.createUser();
    }

    public static void getUsersList(OnCompleteListener<QuerySnapshot> listener) {
        // Get the users list from Firestore
        UserRepository.getUsersList(listener);
    }

    public Task<User> getCurrentUserData() {
        // Get the current user from Firestore and cast it to a User model Object
        return userRepository.getCurrentUserData().continueWith(task -> task.getResult().toObject(User.class)) ;
    }

    public Task<User> getUserData(String id) {
        // Get the user from Firestore and cast it to a User model Object
        return userRepository.getUserData(id).continueWith(task -> task.getResult().toObject(User.class)) ;
    }

    public Task<Void> updateSelectionId(String selectionId) {
        return userRepository.updateSelectionId(selectionId);
    }

    public Task<Void> updateSelectionDate(String selectionDate) {
        return userRepository.updateSelectionDate(selectionDate);
    }

}
