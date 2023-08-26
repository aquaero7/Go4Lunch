package com.example.go4lunch.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.utils.Utils;

import java.util.List;
import java.util.Objects;

public class WorkmatesViewModel extends ViewModel {

    private UserRepository userRepository;
    private RestaurantRepository restaurantRepository;
    private final Utils utils;


    // Constructor for WorkmatesAdapter
    public WorkmatesViewModel() {
        utils = Utils.getInstance();
    }

    // Constructor
    public WorkmatesViewModel(
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
        if ((selectionId != null) && (utils.getCurrentDate().equals(selectionDate))) {
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
                && Objects.equals(utils.getCurrentDate(), workmate.getSelectionDate())) ?
                choiceText + workmate.getSelectionName() : "";
    }

    public List<RestaurantWithDistance> getRestaurants() {
        return restaurantRepository.getRestaurants();
    }

    public List<User> getWorkmates() {
        return userRepository.getWorkmates();
    }

}
