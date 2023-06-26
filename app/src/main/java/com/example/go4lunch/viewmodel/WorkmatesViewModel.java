package com.example.go4lunch.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.DataProcessingUtils;

import java.util.List;
import java.util.Objects;

public class WorkmatesViewModel extends ViewModel {

    private MutableLiveData<List<User>> workmatesMutableLiveData;
    private MutableLiveData<List<RestaurantWithDistance>> restaurantsMutableLiveData;

    private final UserManager userManager;
    private final RestaurantManager restaurantManager;
    private final String currentDate;


    // Constructor
    public WorkmatesViewModel() {
        workmatesMutableLiveData = new MutableLiveData<>();
        restaurantsMutableLiveData = new MutableLiveData<>();

        userManager = UserManager.getInstance();
        restaurantManager = RestaurantManager.getInstance();

        currentDate = DataProcessingUtils.getCurrentDate();
    }

    /************
     * LiveData *
     ************/

    public MutableLiveData<List<User>> getWorkmatesMutableLiveData() {
        // Populate the LiveData
        workmatesMutableLiveData.setValue(getWorkmates());
        return workmatesMutableLiveData;
    }

    public MutableLiveData<List<RestaurantWithDistance>> getRestaurantsMutableLiveData() {
        // Populate the LiveData
        restaurantsMutableLiveData.setValue(getRestaurants());
        return restaurantsMutableLiveData;
    }

    /***********
     * Methods *
     ***********/

    public List<RestaurantWithDistance> getRestaurants() {
        return restaurantManager.getRestaurants();
    }

    public List<User> getWorkmates() {
        return userManager.getWorkmates();
    }

    public RestaurantWithDistance checkWorkmateSelection(User workmate) {
        List<RestaurantWithDistance> restaurants = getRestaurants();
        // Get workmate selection
        String selectionId = workmate.getSelectionId();
        String selectionDate = workmate.getSelectionDate();
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
