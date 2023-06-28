package com.example.go4lunch.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.repository.LikedRestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.model.model.LikedRestaurant;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.utils.DataProcessingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailRestaurantViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final LikedRestaurantRepository likedRestaurantRepository;

    private final String currentDate;


    // Constructor
    public DetailRestaurantViewModel() {
        likedRestaurantRepository = LikedRestaurantRepository.getInstance();
        userRepository = UserRepository.getInstance();

        currentDate = DataProcessingUtils.getCurrentDate();
    }


    /************
     * LiveData *
     ************/

    /*

    public MutableLiveData<User> getCurrentUserMutableLiveData() {
        return userRepository.getCurrentUserMutableLiveData();
    }

    public MutableLiveData<List<LikedRestaurant>> getLikedRestaurantsMutableLiveData() {
        return likedRestaurantRepository.getLikedRestaurantsMutableLiveData();
    }

    public MutableLiveData<List<User>> getWorkmatesMutableLiveData() {
        return userRepository.getWorkmatesMutableLiveData();
    }

    */


    /***********
     * Methods *
     ***********/

    // Actions

    public void createLikedRestaurant(User user, String rId) {
        String uId = user.getUid();
        likedRestaurantRepository.createLikedRestaurant(rId+uId, rId, uId);     // Document in database
        likedRestaurantRepository.updateLikedRestaurants(rId+uId, rId, uId);    // Local list
    }

    public void deleteLikedRestaurant(User user, String rId) {
        String uId = user.getUid();
        likedRestaurantRepository.deleteLikedRestaurant(rId+uId);   // Document in database
        likedRestaurantRepository.updateLikedRestaurants(rId+uId);  // Local list
    }

    public void createSelection(String rId, String rName, String rAddress) {
        // Document in database
        userRepository.updateSelectionId(rId);
        userRepository.updateSelectionDate(currentDate);
        userRepository.updateSelectionName(rName);
        userRepository.updateSelectionAddress(rAddress);
        // Local list
        userRepository.updateCurrentUser(rId, currentDate, rName, rAddress);
        userRepository.updateWorkmates(rId, currentDate, rName, rAddress);
    }

    public void deleteSelection() {
        // Document in database
        userRepository.updateSelectionId(null);
        userRepository.updateSelectionDate(null);
        userRepository.updateSelectionName(null);
        userRepository.updateSelectionAddress(null);
        // Local list
        userRepository.updateCurrentUser(null, null, null, null);
        userRepository.updateWorkmates(null, null, null, null);
    }

    public boolean checkCurrentUserSelection(User currentUser, String rId) {
        String selectionId = currentUser.getSelectionId();
        String selectionDate = currentUser.getSelectionDate();
        return (rId.equals(selectionId)) && (currentDate.equals(selectionDate));
    }

    public boolean checkCurrentUserLikes(User currentUser, List<LikedRestaurant> likedRestaurants) {
        String uId = currentUser.getUid();
        String rId = currentUser.getSelectionId();
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


    // Getters

    public List<User> getSelectors(String rId, List<User> workmates) {
        List<User> selectors = new ArrayList<>();
        // Check selected restaurant id and date
        for (User workmate : workmates) {
            boolean isSelector = (Objects.equals(rId, workmate.getSelectionId())
                    && currentDate.equals(workmate.getSelectionDate()));
            if (isSelector) selectors.add(workmate);
        }
        DataProcessingUtils.sortByName(selectors);
        return selectors;
    }

}
