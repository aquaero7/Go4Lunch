package com.example.go4lunch.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.R;
import com.example.go4lunch.model.api.model.OpeningHours;
import com.example.go4lunch.model.api.model.Photo;
import com.example.go4lunch.model.repository.LocationRepository;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final Utils utils;

    // Constructor
    public ListViewViewModel() {
        userRepository = UserRepository.getInstance();
        restaurantRepository = RestaurantRepository.getInstance();
        utils = Utils.getInstance();
    }


    /************
     * LiveData *
     ************/

    public MutableLiveData<List<User>> getWorkmatesMutableLiveData() {
        return userRepository.getWorkmatesMutableLiveData();
    }

    public MutableLiveData<List<RestaurantWithDistance>> getRestaurantsMutableLiveData() {
        return restaurantRepository.getRestaurantsMutableLiveData();
    }


    /***********
     * Methods *
     ***********/

    // Actions

    public int countSelections(String rId, List<User> workmates) {
        int selectionsCount = 0;
        for (User workmate : workmates) {
            // Check selected restaurant id and date and increase selections count if matches with restaurant id
            boolean isSelected = rId.equals(workmate.getSelectionId())
                    && utils.getCurrentDate().equals(workmate.getSelectionDate());
            if (isSelected) selectionsCount += 1;
        }
        return selectionsCount;
    }

    public String filterList(String query) {
        List<RestaurantWithDistance> filteredRestaurantsList = new ArrayList<>();
        if (query.isEmpty()) {
            // SearchView is cleared and closed
            restaurantRepository.setRestaurantsToDisplay(getRestaurants());
            return null;
        } else {
            // A query is sent from searchView
            for (RestaurantWithDistance restaurant : getRestaurants()) {
                // Switching both strings to lower case to make case insensitive comparison
                if (restaurant.getName().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
                    filteredRestaurantsList.add(restaurant);
            }
            restaurantRepository.setRestaurantsToDisplay(filteredRestaurantsList);
            return (filteredRestaurantsList.isEmpty()) ? MainApplication.getInstance().getString(R.string.info_restaurant_not_found) : null;
        }
    }


    // Getters

    public String getDistance(long distance) {
        return (distance < 10000) ? distance + "m" : distance / 1000 + "km";
    }

    public String getOpeningInfo(OpeningHours openingHours) {
        if (openingHours != null) {
            return (openingHours.isOpenNow()) ?
                    MainApplication.getInstance().getString(R.string.status_open) :
                    MainApplication.getInstance().getString(R.string.status_closed);
        } else {
            return MainApplication.getInstance().getString(R.string.status_unknown);
        }
    }

    public String getPhotoUrl(List<Photo> photos) {
        return (photos != null) ? photos.get(0).getPhotoUrl(MainApplication.getInstance().getString(R.string.MAPS_API_KEY)) : null;
    }

    public List<RestaurantWithDistance> getRestaurantsToDisplay() {
        return restaurantRepository.getRestaurantsToDisplay();
    }

    public List<RestaurantWithDistance> getRestaurants() {
        return restaurantRepository.getRestaurants();
    }

    public List<User> getWorkmates() {
        return userRepository.getWorkmates();
    }


    // Setters

    public void setRestaurantsToDisplay(List<RestaurantWithDistance> restaurants) {
        restaurantRepository.setRestaurantsToDisplay(restaurants);
    }

}
