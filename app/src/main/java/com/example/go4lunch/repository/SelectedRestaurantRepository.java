package com.example.go4lunch.repository;

import android.util.Log;

import com.example.go4lunch.model.SelectedRestaurant;
import com.example.go4lunch.model.api.Photo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class SelectedRestaurantRepository {

    private static volatile SelectedRestaurantRepository instance;

    // Firestore
    private static final String COLLECTION_SELECTED_RESTAURANTS = "selected_restaurants";

    public SelectedRestaurantRepository() {
    }

    public static SelectedRestaurantRepository getInstance() {
        SelectedRestaurantRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (SelectedRestaurantRepository.class) {
            if (instance == null) {
                instance = new SelectedRestaurantRepository();
            }
            return instance;
        }
    }

    // Get the Collection Reference
    private static CollectionReference getSelectedRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_SELECTED_RESTAURANTS);
    }

    // Create selected restaurant in Firestore
    public void createSelectedRestaurant(String id, String name, String address, double rating, List<Photo> photos) {
        SelectedRestaurant selectedRestaurantToCreate = new SelectedRestaurant(id, name, address, rating, photos);
        getSelectedRestaurantsCollection().document(id).set(selectedRestaurantToCreate);
    }

    // Delete selected restaurant in Firestore
    public void deleteSelectedRestaurant(String id) {
        if (id != null) getSelectedRestaurantsCollection().document(id).delete();
    }

    // Get selected restaurants list from Firestore
    public static void getSelectedRestaurantsList(OnCompleteListener<QuerySnapshot> listener) {
        getSelectedRestaurantsCollection().get().addOnCompleteListener(listener);
    }

    // Get selected restaurant data from Firestore
    public static Task<DocumentSnapshot> getSelectedRestaurantData(String id) {
        if(id != null){
            return getSelectedRestaurantsCollection().document(id).get();
        }else{
            return null;
        }
    }

    // Clear the selected restaurants collection
    public static void clearSelectedRestaurantsCollection() {
        getSelectedRestaurantsList(task -> {
            if (task.isSuccessful()) {
                // Get and delete each document in selected restaurants collection
                for (QueryDocumentSnapshot document : task.getResult()) {
                    getSelectedRestaurantsCollection().document(document.getId()).delete();
                }
            } else {
                Log.d("SelectedRestaurantRepository", "Error getting documents: ", task.getException());
            }
        });
    }


}
