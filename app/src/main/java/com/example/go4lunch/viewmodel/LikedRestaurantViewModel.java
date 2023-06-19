package com.example.go4lunch.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.manager.LikedRestaurantManager;
import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.utils.FirestoreUtils;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LikedRestaurantViewModel extends ViewModel {

    private MutableLiveData<List<LikedRestaurant>> mMutableLiveData;
    private LikedRestaurantManager mLikedRestaurantManager;
    private List<LikedRestaurant> likedRestaurantsList = new ArrayList<>();

    public LikedRestaurantViewModel() {
        mLikedRestaurantManager = LikedRestaurantManager.getInstance();
        mMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<LikedRestaurant>> getMutableLiveData() {
        return mMutableLiveData;
    }


    private LikedRestaurant getLikedRestaurantFromDatabaseDocument(QueryDocumentSnapshot document) {
        String id = Objects.requireNonNull(document.getId());
        String rId = Objects.requireNonNull(document.getData().get("rid")).toString();
        String uId = Objects.requireNonNull(document.getData().get("uid")).toString();
        LikedRestaurant likedRestaurantFromData = new LikedRestaurant(id, rId, uId);

        return likedRestaurantFromData;
    }


    public void fetchLikedRestaurants() {
        // Get liked restaurants list from database document
        mLikedRestaurantManager.getLikedRestaurantsList(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    // Get liked restaurants list
                    if (likedRestaurantsList != null) likedRestaurantsList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> likedRestaurantData = document.getData(); // Map data for debug.
                        LikedRestaurant likedRestaurantToAdd = getLikedRestaurantFromDatabaseDocument(document);
                        likedRestaurantsList.add(likedRestaurantToAdd);
                    }
                    mMutableLiveData.setValue(likedRestaurantsList);
                }
            } else {
                Log.d("LikedRestaurantViewModel", "Error getting documents: ", task.getException());
            }
        });
    }

}
