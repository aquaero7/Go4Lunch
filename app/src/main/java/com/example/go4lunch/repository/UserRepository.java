package com.example.go4lunch.repository;

import android.content.Context;
import android.util.Log;

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
    private static final String USER_ID = "uid";
    private static final String USER_NAME = "username";
    private static final String USER_EMAIL = "userEmail";
    private static final String USER_URL_PICTURE = "userUrlPicture";
    private static final String SELECTION_ID_FIELD = "selectionId";
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

    @Nullable public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    // Get current user ID
    @Nullable public String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return (user != null)? user.getUid() : null;
    }

    // Get the Collection Reference
    private static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    // Create user in Firestore
    public void createUser() {
        FirebaseUser cUser = getCurrentUser();
        if(cUser != null){
            // Data from FirebaseAuth
            String userUrlPicture = (cUser.getPhotoUrl() != null) ? cUser.getPhotoUrl().toString() : null;
            String username = cUser.getDisplayName();
            String userEmail = cUser.getEmail();
            String uid = cUser.getUid();

            // If the current user already exist in Firestore, we get his data from Firestore
            Task<DocumentSnapshot> userData = getCurrentUserData();
            userData.continueWith(task -> task.getResult().toObject(User.class))
                    .addOnSuccessListener(user -> {
                if (user != null) {
                    // If the current user already exist in Firestore, we update his data
                    getUsersCollection().document(uid)
                            .update(USER_ID, uid, USER_NAME, username, USER_EMAIL, userEmail,
                                    USER_URL_PICTURE, userUrlPicture)
                            .addOnSuccessListener(command -> Log.w("UserRepository",
                                    "Update successful"))
                            .addOnFailureListener(e -> Log.w("UserRepository",
                                    "Update failed. Message : " + e.getMessage()));
                    Log.w("UserRepository", "User already exists");
                } else {
                    // If the current user doesn't exist in Firestore, we create this user
                    // This method erases selectionId field
                    User userToCreate = new User(uid, username, userEmail, userUrlPicture);
                    getUsersCollection().document(uid).set(userToCreate);
                    Log.w("UserRepository", "User didn't exist and has been created");
                }
            });
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
    public Task<Void> updateSelectionId(String selectionId) {
        String uid = this.getCurrentUserUID();
        if(uid != null){
            return this.getUsersCollection().document(uid).update(SELECTION_ID_FIELD, selectionId);
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
