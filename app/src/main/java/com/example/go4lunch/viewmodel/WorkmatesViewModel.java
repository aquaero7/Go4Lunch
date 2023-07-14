package com.example.go4lunch.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.repository.LocationRepository;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.utils.Utils;

import java.util.List;
import java.util.Objects;

public class WorkmatesViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    // Constructor
    public WorkmatesViewModel() {
        userRepository = UserRepository.getInstance();
        restaurantRepository = RestaurantRepository.getInstance();
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

    // Getters

    public RestaurantWithDistance getWorkmateSelection(int position) {
        // Get workmate selection
        String selectionId = getWorkmates().get(position).getSelectionId();
        String selectionDate = getWorkmates().get(position).getSelectionDate();
        RestaurantWithDistance selectedRestaurant = null;
        // If a restaurant is selected, get it from restaurants list and launch detail activity
        if ((selectionId != null) && (Utils.getCurrentDate().equals(selectionDate))) {
            for (RestaurantWithDistance restaurant : getRestaurants()) {
                if (Objects.equals(selectionId, restaurant.getRid())) {
                    selectedRestaurant = restaurant;
                    break;
                }
            }
        }
        return selectedRestaurant;
    }

    public String getTextAndChoice(String choiceText, User workmate) {
        return (workmate.getSelectionName() != null
                && workmate.getSelectionDate() != null
                && Objects.equals(Utils.getCurrentDate(), workmate.getSelectionDate())) ?
                choiceText + workmate.getSelectionName() : "";
    }

    public List<RestaurantWithDistance> getRestaurants() {
        return restaurantRepository.getRestaurants();
    }

    public List<User> getWorkmates() {
        return userRepository.getWorkmates();
    }

}
