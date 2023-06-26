package com.example.go4lunch.viewmodel;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.manager.LikedRestaurantManager;
import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;

public class DrawerViewModel extends ViewModel {

    private MutableLiveData<User> currentUserMutableLiveData;
    private MutableLiveData<List<RestaurantWithDistance>> restaurantsMutableLiveData;
    private MutableLiveData<List<LikedRestaurant>> likedRestaurantsMutableLiveData;
    private final UserManager userManager;
    private final RestaurantManager restaurantManager;
    private final LikedRestaurantManager likedRestaurantManager;
    private final String currentDate;


    // Constructor
    public DrawerViewModel() {
        currentUserMutableLiveData = new MutableLiveData<>();
        restaurantsMutableLiveData = new MutableLiveData<>();
        likedRestaurantsMutableLiveData = new MutableLiveData<>();

        userManager = UserManager.getInstance();
        restaurantManager = RestaurantManager.getInstance();
        likedRestaurantManager = LikedRestaurantManager.getInstance();

        currentDate = DataProcessingUtils.getCurrentDate();
    }

    /************
     * LiveData *
     ************/

    public MutableLiveData<User> getCurrentUserMutableLiveData() {
        // Populate the LiveData
        currentUserMutableLiveData.setValue(getCurrentUser());
        return currentUserMutableLiveData;
    }

    public MutableLiveData<List<RestaurantWithDistance>> getRestaurantsMutableLiveData() {
        // Populate the LiveData
        restaurantsMutableLiveData.setValue(getRestaurants());
        return restaurantsMutableLiveData;
    }

    public MutableLiveData<List<LikedRestaurant>> getLikedRestaurantsMutableLiveData() {
        // Populate the LiveData
        likedRestaurantsMutableLiveData.setValue(getLikedRestaurants());
        return likedRestaurantsMutableLiveData;
    }


    /***********
     * Methods *
     ***********/

    public void fetchWorkmates() {
        userManager.fetchWorkmates();
    }

    public void fetchCurrentLocationAndRestaurants(Activity activity, String apiKey) {
        restaurantManager.fetchCurrentLocationAndRestaurants(activity, apiKey, getSearchRadius());
    }

    public void fetchLikedRestaurants() {
        likedRestaurantManager.fetchLikedRestaurants();
    }


    public String getSearchRadius() {
        String searchRadius = getCurrentUser().getSearchRadiusPrefs();
        return (searchRadius != null) ? searchRadius : restaurantManager.getDefaultRadius();
    }

    public List<RestaurantWithDistance> getRestaurants() {
        return restaurantManager.getRestaurants();
    }

    public List<LikedRestaurant> getLikedRestaurants() {
        return likedRestaurantManager.getLikedRestaurants();
    }

    public boolean isFbCurrentUserLogged() {
        return userManager.isFbCurrentUserLogged();
    }

    public FirebaseUser getFbCurrentUser() {
        return userManager.getFbCurrentUser();
    }

    public User getCurrentUser() {
        return userManager.getCurrentUser();
    }

    public void deleteUser() {
        userManager.deleteUser(getCurrentUser().getUid());
    }

    public Task<Void> deleteFbUser(Context context) {
        return userManager.deleteFbUser(context);
    }

    public Task<Void> signOut(Context context) {
        return userManager.signOut(context);
    }


    public RestaurantWithDistance checkCurrentUserSelection() {
        User currentUser = getCurrentUser();
        List<RestaurantWithDistance> restaurants = getRestaurants();
        // Get current user selected restaurant
        String selectionId = currentUser.getSelectionId();
        String selectionDate = currentUser.getSelectionDate();
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

    public void deleteUserLikes() {
        String uId = getCurrentUser().getUid();
        List<LikedRestaurant> likedRestaurants = getLikedRestaurants();
        for (LikedRestaurant likedRestaurant : likedRestaurants) {
            if (likedRestaurant.getUid().equals(uId)) {
                likedRestaurantManager.deleteLikedRestaurant(likedRestaurant.getId());
            }
        }
    }

}
