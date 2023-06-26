package com.example.go4lunch.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import java.util.Collections;
import java.util.List;

public class MapViewViewModel extends ViewModel {

    private MutableLiveData<LatLng> currentLocationMutableLiveData;
    private MutableLiveData<List<RestaurantWithDistance>> restaurantsMutableLiveData;
    private MutableLiveData<List<User>> workmatesMutableLiveData;

    private final RestaurantManager restaurantManager;
    private final UserManager userManager;


    // Constructor
    public MapViewViewModel() {
        currentLocationMutableLiveData = new MutableLiveData<>();
        restaurantsMutableLiveData = new MutableLiveData<>();
        workmatesMutableLiveData = new MutableLiveData<>();

        restaurantManager = RestaurantManager.getInstance();
        userManager = UserManager.getInstance();
    }

    /************
     * LiveData *
     ************/

    public MutableLiveData<LatLng> getCurrentLocationMutableLiveData() {
        // Populate the LiveData
        currentLocationMutableLiveData.setValue(getCurrentLocation());
        return currentLocationMutableLiveData;
    }

    public MutableLiveData<List<RestaurantWithDistance>> getRestaurantsMutableLiveData() {
        // Populate the LiveData
        restaurantsMutableLiveData.setValue(getRestaurants());
        return restaurantsMutableLiveData;
    }

    public MutableLiveData<List<User>> getWorkmatesMutableLiveData() {
        // Populate the LiveData
        workmatesMutableLiveData.setValue(getWorkmates());
        return workmatesMutableLiveData;
    }

    /***********
     * Methods *
     ***********/

    public void initializeAutocompleteSupportFragment(AutocompleteSupportFragment autocompleteFragment) {
        // Specify the types of place data to return.
        // autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setPlaceFields(Collections.singletonList(Place.Field.ID));
        // Specify the type values of place data to return.
        autocompleteFragment.setTypesFilter(Collections.singletonList("restaurant"));
        // Specify the country of place data to return.
        autocompleteFragment.setCountries("FR");
    }

    public boolean arePermissionsGranted() {
        return userManager.arePermissionsGranted();
    }

    public LatLng getDefaultLocation() {
        return restaurantManager.getDefaultLocation();
    }

    public LatLng getCurrentLocation() {
        return restaurantManager.getCurrentLocation();
    }

    public String getSearchRadius() {
        String searchRadius = getCurrentUser().getSearchRadiusPrefs();
        return (searchRadius != null) ? searchRadius : restaurantManager.getDefaultRadius();
    }

    public User getCurrentUser() {
        return userManager.getCurrentUser();
    }

    public List<RestaurantWithDistance> getRestaurants() {
        return restaurantManager.getRestaurants();
    }

    public List<User> getWorkmates() {
        return userManager.getWorkmates();
    }

}
