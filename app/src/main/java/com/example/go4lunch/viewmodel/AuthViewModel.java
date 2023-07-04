package com.example.go4lunch.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.R;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.model.repository.LikedRestaurantRepository;
import com.example.go4lunch.model.repository.LocationRepository;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.google.android.gms.maps.model.LatLng;

public class AuthViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RestaurantRepository restaurantRepository;
    private final LikedRestaurantRepository likedRestaurantRepository;
    // private final Application application; // Only if AuthViewModel extends AndroidViewModel

    // Constructor
    public AuthViewModel(/*@NonNull Application application*/) { // Only if AuthViewModel extends AndroidViewModel
        // super(application); // Only if AuthViewModel extends AndroidViewModel
        // this.application = application; // Only if AuthViewModel extends AndroidViewModel

        userRepository = UserRepository.getInstance();
        locationRepository = LocationRepository.getInstance();
        restaurantRepository = RestaurantRepository.getInstance();
        likedRestaurantRepository = LikedRestaurantRepository.getInstance();
    }


    /************
     * LiveData *
     ************/

    public MutableLiveData<LatLng> getCurrentLocationMutableLiveData() {
        return locationRepository.getCurrentLocationMutableLiveData();
    }


    /***********
     * Methods *
     ***********/

    // Fetchers (using Maps and Firebase APIs)

    public void fetchOtherData() {
        fetchCurrentUser();   // Fetch current user
        fetchWorkmates();     // Fetch workmates list
        fetchLikedRestaurants();  // Fetch liked restaurants list
    }

    public void fetchCurrentUser() {
        userRepository.fetchCurrentUser();
    }

    public void fetchWorkmates() {
        userRepository.fetchWorkmates();
    }

    public void fetchCurrentLocation() {
        // locationRepository.fetchCurrentLocation(application.getApplicationContext()); // Only if AuthViewModel extends AndroidViewModel
        locationRepository.fetchCurrentLocation(MainApplication.getContext());
    }

    public void fetchRestaurants(LatLng home, String apiKey) {
        userRepository.getCurrentUserData()
                .addOnSuccessListener(user -> {
                    restaurantRepository.fetchRestaurants(home, getSearchRadius(user), apiKey);
                    Log.w("MainViewModel", "user radius: " + getSearchRadius(user));
                })
                .addOnFailureListener(e -> {
                    Log.w("MainViewModel", e.getMessage());
                });
    }

    public void fetchLikedRestaurants() {
        likedRestaurantRepository.fetchLikedRestaurants();
    }


    // Getters

    public String getSearchRadius(User user) {
        String searchRadius = user.getSearchRadiusPrefs();
        return (searchRadius != null) ? searchRadius : restaurantRepository.getDefaultRadius();
    }


}
