package com.example.go4lunch.viewmodel;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.repository.LikedRestaurantRepository;
import com.example.go4lunch.repository.LocationRepository;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.UserRepository;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class ListViewViewModel extends ViewModel {

    // private MutableLiveData<List<User>> workmatesMutableLiveData;
    // private MutableLiveData<List<RestaurantWithDistance>> restaurantsMutableLiveData;

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RestaurantRepository restaurantRepository;
    private final LikedRestaurantRepository likedRestaurantRepository;


    // Constructor
    public ListViewViewModel() {
        // workmatesMutableLiveData = new MutableLiveData<>();
        // restaurantsMutableLiveData = new MutableLiveData<>();

        userRepository = UserRepository.getInstance();
        locationRepository = LocationRepository.getInstance();
        restaurantRepository = RestaurantRepository.getInstance();
        likedRestaurantRepository = LikedRestaurantRepository.getInstance();
    }


    /************
     * LiveData *
     ************/

    public MutableLiveData<User> getCurrentUserMutableLiveData() {
        return userRepository.getCurrentUserMutableLiveData();
    }

    public MutableLiveData<List<User>> getWorkmatesMutableLiveData() {
        return userRepository.getWorkmatesMutableLiveData();
    }

    public MutableLiveData<LatLng> getCurrentLocationMutableLiveData() {
        return locationRepository.getCurrentLocationMutableLiveData();
    }

    public MutableLiveData<List<RestaurantWithDistance>> getRestaurantsMutableLiveData() {
        return restaurantRepository.getRestaurantsMutableLiveData();
    }

    public MutableLiveData<List<LikedRestaurant>> getLikedRestaurantsMutableLiveData() {
        return likedRestaurantRepository.getLikedRestaurantsMutableLiveData();
    }

}
