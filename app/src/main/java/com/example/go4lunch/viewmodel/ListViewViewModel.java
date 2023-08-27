package com.example.go4lunch.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.R;
import com.example.go4lunch.model.api.model.OpeningHours;
import com.example.go4lunch.model.api.model.Photo;
import com.example.go4lunch.model.model.Restaurant;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewViewModel extends ViewModel {

    private UserRepository userRepository;
    private RestaurantRepository restaurantRepository;
    private final Utils utils;


    // Constructor for ListViewAdapter
    public ListViewViewModel() {
        utils = Utils.getInstance();
    }

    // Constructor
    public ListViewViewModel(
            UserRepository userRepository, RestaurantRepository restaurantRepository, Utils utils) {

        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.utils = utils;
    }


    /************
     * LiveData *
     ************/

    public MutableLiveData<List<User>> getWorkmatesMutableLiveData() {
        return userRepository.getWorkmatesMutableLiveData();
    }

    public MutableLiveData<List<Restaurant>> getRestaurantsMutableLiveData() {
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

    public String filterList(String query, Context context) {
        List<Restaurant> filteredRestaurantsList = new ArrayList<>();
        if (query.isEmpty()) {
            // SearchView is cleared and closed
            restaurantRepository.setRestaurantsToDisplay(getRestaurants());
            return null;
        } else {
            // A query is sent from searchView
            for (Restaurant restaurant : getRestaurants()) {
                // Switching both strings to lower case to make case insensitive comparison
                if (restaurant.getName().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
                    filteredRestaurantsList.add(restaurant);
            }
            restaurantRepository.setRestaurantsToDisplay(filteredRestaurantsList);
            return (filteredRestaurantsList.isEmpty()) ? context.getString(R.string.info_restaurant_not_found) : null;
        }
    }


    // Getters

    public String getDistance(long distance) {
        return (distance < 10000) ? distance + "m" : distance / 1000 + "km";
    }

    public String getOpeningInfo(OpeningHours openingHours, Context context) {
        if (openingHours != null) {
            return (openingHours.isOpenNow()) ?
                    context.getString(R.string.status_open) :
                    context.getString(R.string.status_closed);
        } else {
            return context.getString(R.string.status_unknown);
        }
    }

    public String getPhotoUrl(List<Photo> photos, Context context) {
        return (photos != null) ? photos.get(0).getPhotoUrl(context.getString(R.string.MAPS_API_KEY)) : null;
    }

    public List<Restaurant> getRestaurantsToDisplay() {
        return restaurantRepository.getRestaurantsToDisplay();
    }

    public List<Restaurant> getRestaurants() {
        return restaurantRepository.getRestaurants();
    }

    public List<User> getWorkmates() {
        return userRepository.getWorkmates();
    }


    // Setters

    public void setRestaurantsToDisplay(List<Restaurant> restaurants) {
        restaurantRepository.setRestaurantsToDisplay(restaurants);
    }

}
