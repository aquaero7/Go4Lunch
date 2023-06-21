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
    private static final String SELECTION_NAME_FIELD = "selectionName";
    private static final String SELECTION_ADDRESS_FIELD = "selectionAddress";
    private static final String SEARCH_RADIUS_PREFS = "searchRadiusPrefs";
    private static final String NOTIFICATIONS_PREFS = "notificationsPrefs";

    public UserRepository() { }

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

    // Get the Collection Reference
    public CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    @Nullable public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // Get current user ID
    @Nullable public String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return (user != null)? user.getUid() : null;
    }

    // Get restaurants list from Firestore
    public void getUsersList(OnCompleteListener<QuerySnapshot> listener) {
        getUsersCollection().get().addOnCompleteListener(listener);
    }

    // Get current User Data from Firestore
    public Task<DocumentSnapshot> getCurrentUserData(){
        String uid = this.getCurrentUserUID();
        if(uid != null){
            return getUsersCollection().document(uid).get();
        }else{
            return null;
        }
    }

    // Update selected restaurant
    public Task<Void> updateSelectionId(String selectionId) {
        String uid = this.getCurrentUserUID();
        if(uid != null){
            return getUsersCollection().document(uid).update(SELECTION_ID_FIELD, selectionId);
        }else{
            return null;
        }
    }

    // Update selection date
    public Task<Void> updateSelectionDate(String selectionDate) {
        String uid = this.getCurrentUserUID();
        if(uid != null){
            return getUsersCollection().document(uid).update(SELECTION_DATE_FIELD, selectionDate);
        }else{
            return null;
        }
    }

    // Update selection name
    public Task<Void> updateSelectionName(String selectionName) {
        String uid = this.getCurrentUserUID();
        if(uid != null){
            return getUsersCollection().document(uid).update(SELECTION_NAME_FIELD, selectionName);
        }else{
            return null;
        }
    }

    public Task<Void> updateSelectionAddress(String selectionAddress) {
        String uid = this.getCurrentUserUID();
        if(uid != null){
            return getUsersCollection().document(uid).update(SELECTION_ADDRESS_FIELD, selectionAddress);
        }else{
            return null;
        }
    }

    // Update selection date
    public Task<Void> updateSearchRadiusPrefs(String searchRadiusPrefs) {
        String uid = this.getCurrentUserUID();
        if(uid != null){
            return getUsersCollection().document(uid).update(SEARCH_RADIUS_PREFS, searchRadiusPrefs);
        }else{
            return null;
        }
    }

    // Update selection date
    public Task<Void> updateNotificationsPrefs(String notificationsPrefs) {
        String uid = this.getCurrentUserUID();
        if(uid != null){
            return getUsersCollection().document(uid).update(NOTIFICATIONS_PREFS, notificationsPrefs);
        }else{
            return null;
        }
    }

    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    public void deleteUser (String id) {
        getUsersCollection().document(id).delete();
    }

    public Task<Void> deleteFirebaseUser(Context context){
        return AuthUI.getInstance().delete(context);
    }

}
