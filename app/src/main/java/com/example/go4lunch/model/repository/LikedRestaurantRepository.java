package com.example.go4lunch.model.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.model.LikedRestaurant;
import com.example.go4lunch.model.helper.LikedRestaurantHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LikedRestaurantRepository {

    private static volatile LikedRestaurantRepository instance;
    private final LikedRestaurantHelper likedRestaurantHelper;

    private final MutableLiveData<List<LikedRestaurant>> likedRestaurantsMutableLiveData;
    private final List<LikedRestaurant> likedRestaurantsList = new ArrayList<>();
    private boolean restaurantIsLiked;


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

    /** For test use only : LikedRestaurantHelper dependency injection and new instance factory */
    private LikedRestaurantRepository(LikedRestaurantHelper likedRestaurantHelper) {
        this.likedRestaurantHelper = likedRestaurantHelper;

        likedRestaurantsMutableLiveData = new MutableLiveData<>();
    }

    public static LikedRestaurantRepository getNewInstance(LikedRestaurantHelper likedRestaurantHelper) {
        instance = new LikedRestaurantRepository(likedRestaurantHelper);
        return instance;
    }
    /********************************************************************************************/


    // Create liked restaurant in Firestore
    public void createLikedRestaurant(String id, String rid, String uid) {
        likedRestaurantHelper.createLikedRestaurant(id, rid, uid);
    }

    // Get the liked restaurants list from Firestore
    public void getLikedRestaurantsList(OnCompleteListener<QuerySnapshot> listener) {
        likedRestaurantHelper.getLikedRestaurantsList(listener);
    }

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
                    if (!likedRestaurantsList.isEmpty()) likedRestaurantsList.clear();
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
    }

    public MutableLiveData<List<LikedRestaurant>> getLikedRestaurantsMutableLiveData() {
        return likedRestaurantsMutableLiveData;
    }

    // Update local list
    public void updateLikedRestaurants(String id, String rId, String uId) {
        likedRestaurantsList.add(new LikedRestaurant(id, rId, uId));
        // Populate the LiveData
        likedRestaurantsMutableLiveData.setValue(likedRestaurantsList);
    }

    // Update local list
    public void updateLikedRestaurants(String id) {
        for (LikedRestaurant likedRestaurant : likedRestaurantsList) {
            if (Objects.equals(id, likedRestaurant.getId())) {
                likedRestaurantsList.remove(likedRestaurant);
                // Populate the LiveData
                likedRestaurantsMutableLiveData.setValue(likedRestaurantsList);
                break;
            }
        }
    }

    public void setRestaurantLiked(boolean liked) {
        restaurantIsLiked = liked;
    }

    public boolean isRestaurantLiked() {
        return restaurantIsLiked;
    }

    public List<LikedRestaurant> getLikedRestaurants() {
        return likedRestaurantsList;
    }

}
