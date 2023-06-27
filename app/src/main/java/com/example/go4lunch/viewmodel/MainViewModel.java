package com.example.go4lunch.viewmodel;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.repository.LikedRestaurantRepository;
import com.example.go4lunch.repository.LocationRepository;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.UserRepository;
import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;

public class MainViewModel extends ViewModel {

    // private MutableLiveData<User> currentUserMutableLiveData;
    // private MutableLiveData<List<RestaurantWithDistance>> restaurantsMutableLiveData;
    // private MutableLiveData<List<LikedRestaurant>> likedRestaurantsMutableLiveData;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RestaurantRepository restaurantRepository;
    private final LikedRestaurantRepository likedRestaurantRepository;
    private final String currentDate;


    // Constructor
    public MainViewModel() {
        // currentUserMutableLiveData = new MutableLiveData<>();
        // restaurantsMutableLiveData = new MutableLiveData<>();
        // likedRestaurantsMutableLiveData = new MutableLiveData<>();

        userRepository = UserRepository.getInstance();
        locationRepository = LocationRepository.getInstance();
        restaurantRepository = RestaurantRepository.getInstance();
        likedRestaurantRepository = LikedRestaurantRepository.getInstance();

        currentDate = DataProcessingUtils.getCurrentDate();
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

    public MutableLiveData<List<LikedRestaurant>> getLikedRestaurantsMutableLiveData() {
        return likedRestaurantRepository.getLikedRestaurantsMutableLiveData();
    }


    /***********
     * Methods *
     ***********/

    // Fetchers (using Maps and Firebase APIs)

    public void fetchCurrentUser() {
        userRepository.fetchCurrentUser();
    }

    public void fetchWorkmates() {
        userRepository.fetchWorkmates();
    }

    public void fetchCurrentLocation(Activity activity) {
        locationRepository.fetchCurrentLocation(activity);
    }

    public void fetchRestaurants(LatLng home, String radius, String apiKey) {
        restaurantRepository.fetchRestaurants(home, radius, apiKey);
    }

    public void fetchLikedRestaurants() {
        likedRestaurantRepository.fetchLikedRestaurants();
    }


    // Actions

    public RestaurantWithDistance checkCurrentUserSelection(User user, List<RestaurantWithDistance> restaurants) {
        // Get current user selected restaurant
        String selectionId = user.getSelectionId();
        String selectionDate = user.getSelectionDate();
        RestaurantWithDistance selectedRestaurant = null;
        // If a restaurant is selected, check if selected restaurant is nearby
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

    public void deleteUserLikes(User user, List<LikedRestaurant> likedRestaurants) {
        String uId = user.getUid();
        for (LikedRestaurant likedRestaurant : likedRestaurants) {
            if (likedRestaurant.getUid().equals(uId)) {
                likedRestaurantRepository.deleteLikedRestaurant(likedRestaurant.getId());
            }
        }
    }

    public void deleteUser(User user) {
        userRepository.deleteUser(user.getUid());
    }

    public Task<Void> deleteFbUser(Context context) {
        return userRepository.deleteFbUser(context);
    }

    public Task<Void> signOut(Context context) {
        return userRepository.signOut(context);
    }


    // Getters

    public boolean isFbCurrentUserLogged() {
        return userRepository.isFbCurrentUserLogged();
    }

    public FirebaseUser getFbCurrentUser() {
        return userRepository.getFbCurrentUser();
    }

    public String getSearchRadius(User user) {
        String searchRadius = user.getSearchRadiusPrefs();
        return (searchRadius != null) ? searchRadius : restaurantRepository.getDefaultRadius();
    }

}
