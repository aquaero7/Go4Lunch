package com.example.go4lunch.viewmodel;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.repository.AutocompleteRepository;
import com.example.go4lunch.model.repository.LocationRepository;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.maps.android.SphericalUtil;

import java.util.Collections;
import java.util.List;

public class MapViewViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RestaurantRepository restaurantRepository;
    private final AutocompleteRepository autocompleteRepository;
    private final Utils utils;


    // Constructor
    public MapViewViewModel(
            UserRepository userRepository, LocationRepository locationRepository,
            RestaurantRepository restaurantRepository, AutocompleteRepository autocompleteRepository,
            Utils utils) {

        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.restaurantRepository = restaurantRepository;
        this.autocompleteRepository = autocompleteRepository;
        this.utils = utils;
    }


    /************
     * LiveData *
     ************/

    public MutableLiveData<List<User>> getWorkmatesMutableLiveData() {
        return userRepository.getWorkmatesMutableLiveData();
    }

    public MutableLiveData<LatLng> getCurrentLocationMutableLiveData() {
        return locationRepository.getCurrentLocationMutableLiveData();
    }

    public MutableLiveData<List<RestaurantWithDistance>> getRestaurantsMutableLiveData() {
        return restaurantRepository.getRestaurantsMutableLiveData();
    }


    /***********
     * Methods *
     ***********/

    // Actions

    public LatLngBounds calculateBounds() {
        LatLng home = getCurrentLocation();
        int radius = Integer.parseInt(getSearchRadius()) * 1000;
        /** Distances in meters / Headings in degrees */
        double distanceToCorner = radius * Math.sqrt(2);
        LatLng sw = SphericalUtil.computeOffset(home, distanceToCorner, 225);   // 5*PI/4
        LatLng ne = SphericalUtil.computeOffset(home, distanceToCorner, 45);    // PI/4

        return new LatLngBounds(sw, ne);
    }

    public void launchAutocomplete(String query, Context context) {
        // Calculate bounds
        LatLngBounds latLngBounds = calculateBounds();
        // Launch autocomplete
        Intent intent = new Autocomplete
                .IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Collections.singletonList(Place.Field.ID))
                .setTypesFilter(Collections.singletonList("restaurant"))
                .setCountries(Collections.singletonList("FR"))
                .setLocationRestriction(RectangularBounds.newInstance(latLngBounds.southwest, latLngBounds.northeast))
                .setInitialQuery(query)
                .build(context);
        autocompleteRepository.getStartAutocomplete().launch(intent);
    }


    // Getters

    public boolean arePermissionsGranted() {
        return locationRepository.arePermissionsGranted();
    }

    public String getSearchRadius() {
        return (getCurrentUser().getSearchRadiusPrefs() != null) ?
                getCurrentUser().getSearchRadiusPrefs() : restaurantRepository.getDefaultRadius();
    }

    public boolean getFocusHome() {
        return locationRepository.getFocusHome();
    }

    public LatLng getCurrentLocation() {
        return locationRepository.getCurrentLocation();
    }

    public User getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public int getDefaultZoom() {
        return locationRepository.getDefaultZoom();
    }

    public int getRestaurantZoom() {
        return locationRepository.getRestaurantZoom();
    }

    public List<RestaurantWithDistance> getRestaurants() {
        return restaurantRepository.getRestaurants();
    }

    public LatLng getLatLng(RestaurantWithDistance restaurant) {
        return new LatLng(restaurant.getGeometry().getLocation().getLat(),
                restaurant.getGeometry().getLocation().getLng());
    }

    public int getSelectionsCount(String rId, List<User> workmates) {
        int selectionsCount = 0;
        for (User workmate : workmates) {
            // For each workmate, check selected restaurant and increase selections count if matches with restaurant id
            boolean isSelected = rId.equals(workmate.getSelectionId()) && utils.getCurrentDate().equals(workmate.getSelectionDate());
            if (isSelected) selectionsCount += 1;
        }
        return selectionsCount;
    }

    public ActivityResultLauncher<Intent> getStartAutocomplete() {
        return autocompleteRepository.getStartAutocomplete();
    }


    // Setters

    public void setFocusHome(boolean focusHome) {
        locationRepository.setFocusHome(focusHome);
    }

    public void setStartAutocomplete(ActivityResultLauncher<Intent> startAutocomplete) {
        autocompleteRepository.setStartAutocomplete(startAutocomplete);
    }

}
