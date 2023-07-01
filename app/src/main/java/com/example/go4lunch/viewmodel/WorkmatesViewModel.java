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

import java.util.List;
import java.util.Objects;

public class WorkmatesViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final String currentDate;


    // Constructor
    public WorkmatesViewModel() {
        userRepository = UserRepository.getInstance();
        restaurantRepository = RestaurantRepository.getInstance();

        currentDate = DataProcessingUtils.getCurrentDate();
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
