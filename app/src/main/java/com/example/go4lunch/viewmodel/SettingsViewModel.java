package com.example.go4lunch.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.model.model.User;

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

    // NIL //


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

    public User getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public String getSearchRadius(User user) {
        String searchRadius = user.getSearchRadiusPrefs();
        return (searchRadius != null) ? searchRadius : restaurantRepository.getDefaultRadius();
    }

    public String getNotificationsPrefs(User user) {
        return user.getNotificationsPrefs();
    }

}
