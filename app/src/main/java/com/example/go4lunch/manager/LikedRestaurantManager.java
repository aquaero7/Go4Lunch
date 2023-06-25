package com.example.go4lunch.manager;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.repository.LikedRestaurantRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LikedRestaurantManager {

    private static volatile LikedRestaurantManager instance;
    private final LikedRestaurantRepository likedRestaurantRepository;
    private MutableLiveData<List<LikedRestaurant>> likedRestaurantsMutableLiveData;
    private List<LikedRestaurant> likedRestaurantsList = new ArrayList<>();


    private LikedRestaurantManager() {
        likedRestaurantRepository = LikedRestaurantRepository.getInstance();
        likedRestaurantsMutableLiveData = new MutableLiveData<>();
    }

    public static LikedRestaurantManager getInstance() {
        LikedRestaurantManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(LikedRestaurantRepository.class) {
            if (instance == null) {
                instance = new LikedRestaurantManager();
            }
            return instance;
        }
    }

    // Create liked restaurant in Firestore
    public void createLikedRestaurant(String id, String rid, String uid) {
        likedRestaurantRepository.createLikedRestaurant(id, rid, uid);
    }

    // Get the liked restaurants list from Firestore
    public void getLikedRestaurantsList(OnCompleteListener<QuerySnapshot> listener) {
        likedRestaurantRepository.getLikedRestaurantsList(listener);
    }

    /* Get the liked restaurant data from Firestore and cast it to a LikedRestaurant model Object
    public Task<LikedRestaurant> getLikedRestaurantData(String id) {
        return likedRestaurantRepository.getLikedRestaurantData(id)
                .continueWith(task -> task.getResult().toObject(LikedRestaurant.class));
    }
    */

    // Delete liked restaurant in Firestore
    public void deleteLikedRestaurant(String id) {
        likedRestaurantRepository.deleteLikedRestaurant(id);
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
                Log.d("LikedRestaurantManager", "Error getting documents: ", task.getException());
            }
        });
    }

    public List<LikedRestaurant> getLikedRestaurants() {
        return likedRestaurantsList;
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

}
