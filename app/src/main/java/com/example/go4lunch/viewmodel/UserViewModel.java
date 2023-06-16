package com.example.go4lunch.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.UserRepository;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserViewModel extends ViewModel {

    private UserRepository mUserRepository;
    private UserManager mUserManager;
    private MutableLiveData<List<User>> mMutableLiveData;
    private List<User> workmatesList = new ArrayList<>();

    public UserViewModel() {
        mUserManager = UserManager.getInstance();
        mMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<User>> getMutableLiveData() {
        return mMutableLiveData;
    }


    private User getUserFromDatabaseDocument(QueryDocumentSnapshot document) {
        String uId = Objects.requireNonNull(document.getData().get("uid")).toString();
        String uName = Objects.requireNonNull(document.getData().get("username")).toString();
        String uEmail = ((document.getData().get("userEmail")) != null) ? document.getData().get("userEmail").toString() : null;
        String uUrlPicture = ((document.getData().get("userUrlPicture")) != null) ? document.getData().get("userUrlPicture").toString() : null;
        String selectionId = ((document.getData().get("selectionId")) != null) ? document.getData().get("selectionId").toString() : null;
        String selectionDate = ((document.getData().get("selectionDate")) != null) ? document.getData().get("selectionDate").toString() : null;
        String searchRadiusPrefs = ((document.getData().get("searchRadiusPrefs")) != null) ? document.getData().get("searchRadiusPrefs").toString() : null;
        String notificationsPrefs = ((document.getData().get("notificationsPrefs")) != null) ? document.getData().get("notificationsPrefs").toString() : null;

        User userFromData = new User(uId, uName, uEmail, uUrlPicture, selectionId, selectionDate, searchRadiusPrefs, notificationsPrefs);

        return userFromData;
    }

    public void fetchWorkmates() {
        // Get workmates list from database document
        mUserManager.getUsersList(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    // Get users list
                    if (workmatesList != null) workmatesList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> userData = document.getData(); // Map data for debug.
                        User workmateToAdd = getUserFromDatabaseDocument(document);
                        workmatesList.add(workmateToAdd);
                    }
                    mMutableLiveData.setValue(workmatesList);
                }
            } else {
                Log.w("UserViewModel", "Error getting documents: ", task.getException());
            }
        });
    }




}
