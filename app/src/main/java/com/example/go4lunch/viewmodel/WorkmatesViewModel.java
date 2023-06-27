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
import com.example.go4lunch.utils.DataProcessingUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Objects;

public class WorkmatesViewModel extends ViewModel {

    // private MutableLiveData<List<User>> workmatesMutableLiveData;
    // private MutableLiveData<List<RestaurantWithDistance>> restaurantsMutableLiveData;

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RestaurantRepository restaurantRepository;
    private final LikedRestaurantRepository likedRestaurantRepository;

    private final String currentDate;


    // Constructor
    public WorkmatesViewModel() {
        // workmatesMutableLiveData = new MutableLiveData<>();
        // restaurantsMutableLiveData = new MutableLiveData<>();

        userRepository = UserRepository.getInstance();
        locationRepository = LocationRepository.getInstance();
        restaurantRepository = RestaurantRepository.getInstance();
        likedRestaurantRepository = LikedRestaurantRepository.getInstance();

        currentDate = DataProcessingUtils.getCurrentDate();
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


    /***********
     * Methods *
     ***********/

    // Action

    public RestaurantWithDistance checkWorkmateSelection(User user, List<RestaurantWithDistance> restaurants) {
        // Get workmate selection
        String selectionId = user.getSelectionId();
        String selectionDate = user.getSelectionDate();
        RestaurantWithDistance selectedRestaurant = null;
        // If a restaurant is selected, get it from restaurants list and launch detail activity
        if ((selectionId != null) && (currentDate.equals(selectionDate))) {
            for (RestaurantWithDistance restaurant : restaurants) {
                if (Objects.equals(selectionId, restaurant.getRid())) {
                    selectedRestaurant = restaurant;
                    break;
                }
            }
        }
        return selectedRestaurant;
    }

}
