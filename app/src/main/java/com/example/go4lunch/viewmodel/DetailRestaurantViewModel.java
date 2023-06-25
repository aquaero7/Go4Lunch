package com.example.go4lunch.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.go4lunch.manager.LikedRestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.CalendarUtils;
import com.example.go4lunch.utils.DataProcessingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailRestaurantViewModel extends ViewModel {

    private final LikedRestaurantManager likedRestaurantManager;
    private final UserManager userManager;
    private final String currentDate;


    // Constructor
    public DetailRestaurantViewModel() {
        likedRestaurantManager = LikedRestaurantManager.getInstance();
        userManager = UserManager.getInstance();

        currentDate = CalendarUtils.getCurrentDate();
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

    public List<User> getWorkmates() {
        return userManager.getWorkmates();
    }

    public List<User> getSelectors(String rId) {
        List<User> selectors = new ArrayList<>();
        List<User> workmates = getWorkmates();
        // Check selected restaurant id and date
        for (User workmate : workmates) {
            boolean isSelector = (Objects.equals(rId, workmate.getSelectionId())
                    && currentDate.equals(workmate.getSelectionDate()));
            if (isSelector) selectors.add(workmate);
        }
        DataProcessingUtils.sortByName(selectors);
        return selectors;
    }

    public List<LikedRestaurant> getLikedRestaurants() {
        return likedRestaurantManager.getLikedRestaurants();
    }

    public void createLikedRestaurant(String rId) {
        String uId = getCurrentUser().getUid();
        likedRestaurantManager.createLikedRestaurant(rId+uId, rId, uId);     // Document in database
        likedRestaurantManager.updateLikedRestaurants(rId+uId, rId, uId);    // Local list
    }

    public void deleteLikedRestaurant(String rId) {
        String uId = getCurrentUser().getUid();
        likedRestaurantManager.deleteLikedRestaurant(rId+uId);   // Document in database
        likedRestaurantManager.updateLikedRestaurants(rId+uId);  // Local list
    }

    public void createSelection(String rId, String rName, String rAddress) {
        // Document in database
        userManager.updateSelectionId(rId);
        userManager.updateSelectionDate(currentDate);
        userManager.updateSelectionName(rName);
        userManager.updateSelectionAddress(rAddress);
        // Local list
        userManager.updateWorkmates(rId, currentDate, rName, rAddress);
    }

    public void deleteSelection() {
        // Document in database
        userManager.updateSelectionId(null);
        userManager.updateSelectionDate(null);
        userManager.updateSelectionName(null);
        userManager.updateSelectionAddress(null);
        // Local list
        userManager.updateWorkmates(null, null, null, null);
    }

    public boolean checkCurrentUserSelection(String rId) {
        User currentUser = getCurrentUser();
        String selectionId = currentUser.getSelectionId();
        String selectionDate = currentUser.getSelectionDate();
        return (rId.equals(selectionId)) && (currentDate.equals(selectionDate));
    }

    public boolean checkCurrentUserLikes(String rId) {
        String uId = getCurrentUser().getUid();
        List<LikedRestaurant> likedRestaurants = getLikedRestaurants();
        boolean isLiked = false;
        if (likedRestaurants != null) {
            for (LikedRestaurant likedRestaurant : likedRestaurants) {
                if (Objects.equals(rId, likedRestaurant.getRid()) && Objects.equals(uId, likedRestaurant.getUid())) {
                    isLiked = true;
                    break;
                }
            }
        }
        return isLiked;
    }



}
