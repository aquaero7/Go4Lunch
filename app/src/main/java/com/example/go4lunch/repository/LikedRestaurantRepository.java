package com.example.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.LikedRestaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LikedRestaurantRepository {

    private static volatile LikedRestaurantRepository instance;
    private final LikedRestaurantHelper likedRestaurantHelper;

    private MutableLiveData<List<LikedRestaurant>> likedRestaurantsMutableLiveData;
    private List<LikedRestaurant> likedRestaurantsList = new ArrayList<>();


    private LikedRestaurantRepository() {
        likedRestaurantHelper = LikedRestaurantHelper.getInstance();
        likedRestaurantsMutableLiveData = new MutableLiveData<>();
    }

    public static LikedRestaurantRepository getInstance() {
        LikedRestaurantRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(LikedRestaurantHelper.class) {
            if (instance == null) {
                instance = new LikedRestaurantRepository();
            }
            return instance;
        }
    }

    // Create liked restaurant in Firestore
    public void createLikedRestaurant(String id, String rid, String uid) {
        likedRestaurantHelper.createLikedRestaurant(id, rid, uid);
    }

    // Get the liked restaurants list from Firestore
    public void getLikedRestaurantsList(OnCompleteListener<QuerySnapshot> listener) {
        likedRestaurantHelper.getLikedRestaurantsList(listener);
    }

    /* Get the liked restaurant data from Firestore and cast it to a LikedRestaurant model Object
    public Task<LikedRestaurant> getLikedRestaurantData(String id) {
        return likedRestaurantHelper.getLikedRestaurantData(id)
                .continueWith(task -> task.getResult().toObject(LikedRestaurant.class));
    }
    */

    // Delete liked restaurant in Firestore
    public void deleteLikedRestaurant(String id) {
        likedRestaurantHelper.deleteLikedRestaurant(id);
    }

    public void fetchLikedRestaurants() {
        // Get liked restaurants list from database document
        getLikedRestaurantsList(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    // Get liked restaurants list
                    if (likedRestaurantsList != null) likedRestaurantsList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Map<String, Object> likedRestaurantData = document.getData(); // Map data for debug.
                        String id = Objects.requireNonNull(document.getId());
                        String rId = Objects.requireNonNull(document.getData().get("rid")).toString();
                        String uId = Objects.requireNonNull(document.getData().get("uid")).toString();

                        LikedRestaurant likedRestaurantToAdd = new LikedRestaurant(id, rId, uId);
                        likedRestaurantsList.add(likedRestaurantToAdd);
                    }
                }
            } else {
                Log.d("LikedRestaurantRepository", "Error getting documents: ", task.getException());
            }
        });
    }

    public MutableLiveData<List<LikedRestaurant>> getLikedRestaurantsMutableLiveData() {
        // Populate the LiveData
        likedRestaurantsMutableLiveData.setValue(likedRestaurantsList);
        return likedRestaurantsMutableLiveData;
    }

    // Update local list
    public void updateLikedRestaurants(String id, String rId, String uId) {
        likedRestaurantsList.add(new LikedRestaurant(id, rId, uId));
    }

    // Update local list
    public void updateLikedRestaurants(String id) {
        for (LikedRestaurant likedRestaurant : likedRestaurantsList) {
            if (Objects.equals(id, likedRestaurant.getId())) likedRestaurantsList.remove(likedRestaurant);
            break;
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    public MutableLiveData<List<LikedRestaurant>> getLikedRestaurantsMutableLiveData() {
        likedRestaurantsMutableLiveData = new MutableLiveData<>();

        // Get liked restaurants list from database document
        getLikedRestaurantsList(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    // Get liked restaurants list
                    if (likedRestaurantsList != null) likedRestaurantsList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Map<String, Object> likedRestaurantData = document.getData(); // Map data for debug.
                        String id = Objects.requireNonNull(document.getId());
                        String rId = Objects.requireNonNull(document.getData().get("rid")).toString();
                        String uId = Objects.requireNonNull(document.getData().get("uid")).toString();

                        LikedRestaurant likedRestaurantToAdd = new LikedRestaurant(id, rId, uId);
                        likedRestaurantsList.add(likedRestaurantToAdd);
                    }
                    // Populate the LiveData
                    likedRestaurantsMutableLiveData.setValue(likedRestaurantsList);
                }
            } else {
                Log.d("LikedRestaurantRepository", "Error getting documents: ", task.getException());
            }
        });
        return likedRestaurantsMutableLiveData;
    }
    */

}
