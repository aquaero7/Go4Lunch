package com.example.go4lunch.repository;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.go4lunch.model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class UserRepository {

    private static volatile UserRepository instance;

    // Firestore
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_RESTAURANTS = "restaurants";
    private static final String SELECTED_RESTAURANT_ID_FIELD = "selectedRestaurantId";
    private static final String SELECTION_DATE_FIELD = "selectionDate";

    private UserRepository() { }

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    // Get current user ID
    @Nullable
    public String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return (user != null)? user.getUid() : null;
    }

    // Get the Collection Reference
    private static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    // Create user in Firestore
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        if(user != null){
            // Data from FirebaseAuth
            String userUrlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String userEmail = user.getEmail();
            String uid = user.getUid();

            User userToCreate = new User(uid, username, userEmail, userUrlPicture);

            // If the user already exist in Firestore, we get his data
            Task<DocumentSnapshot> userData = getCurrentUserData();
            userData.addOnSuccessListener(documentSnapshot -> this.getUsersCollection().document(uid).set(userToCreate));
        }
    }

    // Get restaurants list from Firestore
    public static void getUsersList(OnCompleteListener<QuerySnapshot> listener) {
        getUsersCollection().get().addOnCompleteListener(listener);
    }

    // Get current User Data from Firestore
    public Task<DocumentSnapshot> getCurrentUserData(){
        String uid = this.getCurrentUserUID();
        if(uid != null){
            return this.getUsersCollection().document(uid).get();
        }else{
            return null;
        }
    }

    // Get User Data from Firestore
    public Task<DocumentSnapshot> getUserData(String uid){
        if(uid != null){
            return this.getUsersCollection().document(uid).get();
        }else{
            return null;
        }
    }

    // Update selected restaurant
    public Task<Void> updateSelectedRestaurantId(String selectedRestaurantId) {
        String uid = this.getCurrentUserUID();
        if(uid != null){
            return this.getUsersCollection().document(uid).update(SELECTED_RESTAURANT_ID_FIELD, selectedRestaurantId);
        }else{
            return null;
        }
    }

    // Update selection date
    public Task<Void> updateSelectionDate(String selectionDate) {
        String uid = this.getCurrentUserUID();
        if(uid != null){
            return this.getUsersCollection().document(uid).update(SELECTION_DATE_FIELD, selectionDate);
        }else{
            return null;
        }
    }

}
