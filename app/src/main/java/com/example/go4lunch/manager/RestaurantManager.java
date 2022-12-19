package com.example.go4lunch.manager;

import com.example.go4lunch.repository.RestaurantRepository;

public class RestaurantManager {

    private static volatile RestaurantManager instance;
    private final RestaurantRepository restaurantRepository;

    private RestaurantManager() {
        restaurantRepository = RestaurantRepository.getInstance();
    }

    public static RestaurantManager getInstance() {
        RestaurantManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(RestaurantRepository.class) {
            if (instance == null) {
                instance = new RestaurantManager();
            }
            return instance;
        }
    }

    public void createRestaurant(){
        restaurantRepository.createRestaurant();
    }

    public void createRestaurant(String name) {
        restaurantRepository.createRestaurant(name);
    }

    /*
    public Task<Restaurant> getRestaurantData(){
        // Get the restaurant from Firestore and cast it to a User model Object
        return restaurantRepository.getRestaurantData().continueWith(task -> task.getResult().toObject(Restaurant.class)) ;
    }
    */

}
