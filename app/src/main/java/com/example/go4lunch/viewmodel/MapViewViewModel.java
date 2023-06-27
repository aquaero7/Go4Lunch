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
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import java.util.Collections;
import java.util.List;

public class MapViewViewModel extends ViewModel {

    // private MutableLiveData<LatLng> currentLocationMutableLiveData;
    // private MutableLiveData<List<RestaurantWithDistance>> restaurantsMutableLiveData;
    // private MutableLiveData<List<User>> workmatesMutableLiveData;

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RestaurantRepository restaurantRepository;
    private final LikedRestaurantRepository likedRestaurantRepository;


    // Constructor
    public MapViewViewModel() {
        // currentLocationMutableLiveData = new MutableLiveData<>();
        // restaurantsMutableLiveData = new MutableLiveData<>();
        // workmatesMutableLiveData = new MutableLiveData<>();

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


    /***********
     * Methods *
     ***********/

    // Actions

    public void initializeAutocompleteSupportFragment(AutocompleteSupportFragment autocompleteFragment) {
        // Specify the types of place data to return.
        // autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setPlaceFields(Collections.singletonList(Place.Field.ID));
        // Specify the type values of place data to return.
        autocompleteFragment.setTypesFilter(Collections.singletonList("restaurant"));
        // Specify the country of place data to return.
        autocompleteFragment.setCountries("FR");
    }


    // Getters

    public boolean arePermissionsGranted() {
        return userRepository.arePermissionsGranted();
    }

    public LatLng getDefaultLocation() {
        return locationRepository.getDefaultLocation();
    }

    public String getSearchRadius(User user) {
        String searchRadius = user.getSearchRadiusPrefs();
        return (searchRadius != null) ? searchRadius : restaurantRepository.getDefaultRadius();
    }

}
