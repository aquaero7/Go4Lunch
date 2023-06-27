package com.example.go4lunch.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.UserRepository;
import com.example.go4lunch.model.User;

public class SettingsViewModel extends ViewModel {

    private UserRepository userRepository;
    private RestaurantRepository restaurantRepository;


    // Constructor
    public SettingsViewModel() {
        userRepository = UserRepository.getInstance();
        restaurantRepository = RestaurantRepository.getInstance();
    }


    /************
     * LiveData *
     ************/

    /*
    public MutableLiveData<User> getCurrentUserMutableLiveData() {
        return userRepository.getCurrentUserMutableLiveData();
    }
    */


    /***********
     * Methods *
     ***********/

    // Actions

    public void updateSearchRadiusPrefs(String searchRadiusPrefs) {
            // Update search radius preference to user document in database
            userRepository.updateSearchRadiusPrefs(searchRadiusPrefs);
            // Update search radius preference to local user and local workmates list
            userRepository.updateCurrentUser("RAD", searchRadiusPrefs);
            userRepository.updateWorkmates("RAD", searchRadiusPrefs);
    }

    public void updateNotificationsPrefs(String notificationsPrefs) {
        // Update notifications preference to user document in database
        userRepository.updateNotificationsPrefs(notificationsPrefs);
        // Update notifications preference to local user and local workmates list
        userRepository.updateCurrentUser("NOT", notificationsPrefs);
        userRepository.updateWorkmates("NOT", notificationsPrefs);
    }


    // Getters

    public String getSearchRadius(User user) {
        String searchRadius = user.getSearchRadiusPrefs();
        return (searchRadius != null) ? searchRadius : restaurantRepository.getDefaultRadius();
    }

    public String getNotificationsPrefs(User user) {
        return user.getNotificationsPrefs();
    }

}
