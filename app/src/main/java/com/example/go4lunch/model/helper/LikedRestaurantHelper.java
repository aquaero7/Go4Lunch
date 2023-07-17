package com.example.go4lunch.model.helper;

import com.example.go4lunch.model.model.LikedRestaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LikedRestaurantHelper {

    private static volatile LikedRestaurantHelper instance;

    // Firestore
    private static final String COLLECTION_LIKED_RESTAURANTS = "liked_restaurants";

    public LikedRestaurantHelper() {
    }

    public static LikedRestaurantHelper getInstance() {
        LikedRestaurantHelper result = instance;
        if (result != null) {
            return result;
        }
        synchronized (LikedRestaurantHelper.class) {
            if (instance == null) {
                instance = new LikedRestaurantHelper();
            }
            return instance;
        }
    }

    // Get the Collection Reference
    private CollectionReference getLikedRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_LIKED_RESTAURANTS);
    }

    // Create liked restaurant in Firestore
    public void createLikedRestaurant(String id, String rid, String uid) {
        LikedRestaurant likedRestaurantToCreate = new LikedRestaurant(id, rid, uid);
        getLikedRestaurantsCollection().document(id).set(likedRestaurantToCreate);
    }

    // Get liked restaurants list from Firestore
    public void getLikedRestaurantsList(OnCompleteListener<QuerySnapshot> listener) {
        getLikedRestaurantsCollection().get().addOnCompleteListener(listener);
    }

    // Delete liked restaurant in Firestore
    public void deleteLikedRestaurant(String id) {
        getLikedRestaurantsCollection().document(id).delete();
    }

}
