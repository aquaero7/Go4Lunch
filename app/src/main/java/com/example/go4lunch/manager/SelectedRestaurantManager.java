package com.example.go4lunch.manager;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.repository.SelectedRestaurantRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class SelectedRestaurantManager {

    private static volatile SelectedRestaurantManager instance;
    private final SelectedRestaurantRepository selectedRestaurantRepository;


    private SelectedRestaurantManager() { selectedRestaurantRepository = SelectedRestaurantRepository.getInstance(); }

    public static SelectedRestaurantManager getInstance() {
        SelectedRestaurantManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized (SelectedRestaurantRepository.class) {
            if (instance == null) {
                instance = new SelectedRestaurantManager();
            }
            return instance;
        }
    }

    public void createSelectedRestaurant(String id, String name) {
        selectedRestaurantRepository.createSelectedRestaurant(id, name);
    }

    public void deleteSelectedRestaurant(String id) {
        selectedRestaurantRepository.deleteSelectedRestaurant(id);
    }

    public static void getSelectedRestaurantsList(OnCompleteListener<QuerySnapshot> listener) {
        // Get the selected restaurants list from Firestore
        SelectedRestaurantRepository.getSelectedRestaurantsList(listener);
    }

    public static Task<Restaurant> getRestaurantData(String id) {
        // Get the selected restaurant data from Firestore and cast it to a SelectedRestaurant model Object
        return SelectedRestaurantRepository.getSelectedRestaurantData(id).continueWith(task -> task.getResult().toObject(Restaurant.class));
    }

    // Clear the selected restaurants collection
    public static void clearSelectedRestaurantsCollection() {
        SelectedRestaurantRepository.clearSelectedRestaurantsCollection();
    }









}
