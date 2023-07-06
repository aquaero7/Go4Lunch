package com.example.go4lunch.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.model.repository.LikedRestaurantRepository;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.model.model.LikedRestaurant;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;

public class MainViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final LikedRestaurantRepository likedRestaurantRepository;
    private final String currentDate;
    // private final Application application; // Only if MainViewModel extends AndroidViewModel


    // Constructor
    public MainViewModel(/*@NonNull Application application*/) { // Only if MainViewModel extends AndroidViewModel
        // super(application); // Only if MainViewModel extends AndroidViewModel
        // this.application = application; // Only if MainViewModel extends AndroidViewModel

        userRepository = UserRepository.getInstance();
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

    public MutableLiveData<List<RestaurantWithDistance>> getRestaurantsMutableLiveData() {
        return restaurantRepository.getRestaurantsMutableLiveData();
    }

    public MutableLiveData<List<LikedRestaurant>> getLikedRestaurantsMutableLiveData() {
        return likedRestaurantRepository.getLikedRestaurantsMutableLiveData();
    }


    /***********
     * Methods *
     ***********/

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


    public Task<Void> deleteFbUser() {
        // Method using AuthUI
        // return userRepository.deleteFbUser(application.getApplicationContext()); // Only if MainViewModel extends AndroidViewModel
        return userRepository.deleteFbUser(MainApplication.getContext());
        // Method using FirebaseAuth
        // return userRepository.deleteFbUser();
    }

    // Method using AuthUI
    public Task<Void> signOut() {
        // return userRepository.signOut(application.getApplicationContext()); // Only if MainViewModel extends AndroidViewModel
        return userRepository.signOut(MainApplication.getContext());
    }
    /*
    // Method using FirebaseAuth
    public void signOut() {
        userRepository.signOut();
    }
    */


    // Getters

    public boolean isFbCurrentUserLogged() {
        return userRepository.isFbCurrentUserLogged();
    }

    public FirebaseUser getFbCurrentUser() {
        return userRepository.getFbCurrentUser();
    }


}