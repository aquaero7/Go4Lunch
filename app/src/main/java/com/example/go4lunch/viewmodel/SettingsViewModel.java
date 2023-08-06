package com.example.go4lunch.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.model.model.User;

import java.util.Objects;

public class SettingsViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    // Constructor
    public SettingsViewModel(
            UserRepository userRepository, RestaurantRepository restaurantRepository) {

        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }


    /************
     * LiveData *
     ************/

    // NIL //


    /***********
     * Methods *
     ***********/

    // Actions

    public boolean updateSearchRadiusPrefs(String searchRadiusPrefs) {
        String prefsValue = null;
        boolean searchRadiusStatus = false;
        if (!searchRadiusPrefs.isEmpty() && !Objects.equals(searchRadiusPrefs,"0")) {
            prefsValue = searchRadiusPrefs;
            searchRadiusStatus = true;
        }
        // Update search radius preference to user document in database
        userRepository.updateSearchRadiusPrefs(prefsValue);
        // Update search radius preference to local user and local workmates list
        userRepository.updateCurrentUser("RAD", prefsValue);
        userRepository.updateWorkmates("RAD", prefsValue);

        return searchRadiusStatus;
    }

    public boolean updateNotificationsPrefs(String notificationsPrefs) {
        String prefsValue = null;
        boolean notificationStatus = false;
        if (notificationsPrefs != null && Boolean.parseBoolean(notificationsPrefs)) {
            prefsValue = notificationsPrefs;
            notificationStatus = true;
        }
        // Update notifications preference to user document in database
        userRepository.updateNotificationsPrefs(prefsValue);
        // Update notifications preference to local user and local workmates list
        userRepository.updateCurrentUser("NOT", prefsValue);
        userRepository.updateWorkmates("NOT", prefsValue);

        return notificationStatus;
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
