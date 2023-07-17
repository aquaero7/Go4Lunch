package com.example.go4lunch.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.R;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.model.model.User;

import java.util.Objects;

public class SettingsViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;


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

    public String updateSearchRadiusPrefs(String searchRadiusPrefs) {
        String prefsValue = null;
        String message = MainApplication.getInstance().getString(R.string.search_radius_prefs_deleted);;
        if (!searchRadiusPrefs.isEmpty() && !Objects.equals(searchRadiusPrefs,"0")) {
            prefsValue = searchRadiusPrefs;
            message = MainApplication.getInstance().getString(R.string.search_radius_prefs_saved);
        }
        // Update search radius preference to user document in database
        userRepository.updateSearchRadiusPrefs(prefsValue);
        // Update search radius preference to local user and local workmates list
        userRepository.updateCurrentUser("RAD", prefsValue);
        userRepository.updateWorkmates("RAD", prefsValue);

        return message;
    }

    public String updateNotificationsPrefs(String notificationsPrefs) {
        String prefsValue = null;
        String message = MainApplication.getInstance().getString(R.string.switch_unchecked);
        if (notificationsPrefs != null && Boolean.parseBoolean(notificationsPrefs)) {
            prefsValue = notificationsPrefs;
            message = MainApplication.getInstance().getString(R.string.switch_checked);
        }
        // Update notifications preference to user document in database
        userRepository.updateNotificationsPrefs(prefsValue);
        // Update notifications preference to local user and local workmates list
        userRepository.updateCurrentUser("NOT", prefsValue);
        userRepository.updateWorkmates("NOT", prefsValue);

        return message;
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
