package com.example.go4lunch.manager;

import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.repository.LikedRestaurantRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class LikedRestaurantManager {

    private static volatile LikedRestaurantManager instance;
    private final LikedRestaurantRepository likedRestaurantRepository;


    private LikedRestaurantManager() {
        likedRestaurantRepository = LikedRestaurantRepository.getInstance();
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
    public static void getLikedRestaurantsList(OnCompleteListener<QuerySnapshot> listener) {
        LikedRestaurantRepository.getLikedRestaurantsList(listener);
    }

    /* Get the liked restaurant data from Firestore and cast it to a LikedRestaurant model Object
    public static Task<LikedRestaurant> getLikedRestaurantData(String id) {
        return LikedRestaurantRepository.getLikedRestaurantData(id)
                .continueWith(task -> task.getResult().toObject(LikedRestaurant.class));
    }
    */

    // Delete liked restaurant in Firestore
    public void deleteLikedRestaurant(String id) {
        likedRestaurantRepository.deleteLikedRestaurant(id);
    }

    /* Clear the liked restaurants collection
    public static void clearLikedRestaurantsCollection() {
        LikedRestaurantRepository.clearLikedRestaurantsCollection();
    }
    */

}
