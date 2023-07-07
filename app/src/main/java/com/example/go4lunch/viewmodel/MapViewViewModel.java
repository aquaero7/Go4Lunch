package com.example.go4lunch.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.model.LikedRestaurant;
import com.example.go4lunch.model.repository.LikedRestaurantRepository;
import com.example.go4lunch.model.repository.LocationRepository;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Collections;
import java.util.List;

public class MapViewViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RestaurantRepository restaurantRepository;


    // Constructor
    public MapViewViewModel() {
        userRepository = UserRepository.getInstance();
        locationRepository = LocationRepository.getInstance();
        restaurantRepository = RestaurantRepository.getInstance();
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


    /***********
     * Methods *
     ***********/

    // Actions

    public void initializeAutocompleteSupportFragment(AutocompleteSupportFragment autocompleteFragment, String query) {
        // Specify the types of place data to return.
        // autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setPlaceFields(Collections.singletonList(Place.Field.ID));
        // Specify the type values of place data to return.
        autocompleteFragment.setTypesFilter(Collections.singletonList("restaurant"));
        // Specify the country of place data to return.
        autocompleteFragment.setCountries("FR");
        // Specify the limitation to only show results within the defined region
        LatLngBounds latLngBounds = DataProcessingUtils.calculateBounds(getCurrentLocation(), Integer.parseInt(getSearchRadius())*1000);
        autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(latLngBounds.southwest, latLngBounds.northeast));
        autocompleteFragment.setActivityMode(AutocompleteActivityMode.valueOf("FULLSCREEN"));
        autocompleteFragment.setText(query);
    }


    // Getters

    public boolean arePermissionsGranted() {
        return userRepository.arePermissionsGranted();
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
            boolean isSelected = rId.equals(workmate.getSelectionId()) && DataProcessingUtils.getCurrentDate().equals(workmate.getSelectionDate());
            if (isSelected) selectionsCount += 1;
        }
        return selectionsCount;
    }


    // Setters

    public void setFocusHome(boolean focusHome) {
        locationRepository.setFocusHome(focusHome);
    }

}
