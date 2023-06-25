package com.example.go4lunch.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.go4lunch.R;
import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.MapsApisUtils;

import java.util.Objects;

public class SettingsViewModel extends ViewModel {

    private UserManager userManager;
    private RestaurantManager restaurantManager;

    // Constructor
    public SettingsViewModel() {
        userManager = UserManager.getInstance();
        restaurantManager = RestaurantManager.getInstance();
    }

    /************
     * LiveData *
     ************/

    // NIL

    /***********
     * Methods *
     ***********/

    public User getCurrentUser() {
        return userManager.getCurrentUser();
    }

    public void updateSearchRadiusPrefs(String searchRadiusPrefs) {
            // Update search radius preference to user document in database
            userManager.updateSearchRadiusPrefs(searchRadiusPrefs);
            // Update search radius preference to user into local workmates list
            userManager.updateWorkmates("RAD", searchRadiusPrefs);
    }

    public void updateNotificationsPrefs(String notificationsPrefs) {
        // Update notifications preference to user document in database
        userManager.updateNotificationsPrefs(notificationsPrefs);
        // Update notifications preference to user into local workmates list
        userManager.updateWorkmates("NOT", notificationsPrefs);
    }

    public String getSearchRadius() {
        String searchRadius = getCurrentUser().getSearchRadiusPrefs();
        return (searchRadius != null) ? searchRadius : restaurantManager.getDefaultRadius();
    }


}
