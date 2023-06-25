package com.example.go4lunch.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.manager.LikedRestaurantManager;
import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;

import java.util.List;

public class ListViewViewModel extends ViewModel {

    private MutableLiveData<List<User>> workmatesMutableLiveData;
    private MutableLiveData<List<RestaurantWithDistance>> restaurantsMutableLiveData;

    private final UserManager userManager;
    private final RestaurantManager restaurantManager;


    // Constructor
    public ListViewViewModel() {
        workmatesMutableLiveData = new MutableLiveData<>();
        restaurantsMutableLiveData = new MutableLiveData<>();

        userManager = UserManager.getInstance();
        restaurantManager = RestaurantManager.getInstance();
    }

    /************
     * LiveData *
     ************/

    public MutableLiveData<List<User>> getWorkmatesMutableLiveData() {
        // Populate the LiveData
        workmatesMutableLiveData.setValue(userManager.getWorkmates());
        return workmatesMutableLiveData;
    }

    public MutableLiveData<List<RestaurantWithDistance>> getRestaurantsMutableLiveData() {
        // Populate the LiveData
        restaurantsMutableLiveData.setValue(restaurantManager.getRestaurants());
        return restaurantsMutableLiveData;
    }

}
