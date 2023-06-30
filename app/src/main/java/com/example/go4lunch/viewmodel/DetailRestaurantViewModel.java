package com.example.go4lunch.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.api.model.Period;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.repository.LikedRestaurantRepository;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.model.model.LikedRestaurant;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.utils.DataProcessingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DetailRestaurantViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final LikedRestaurantRepository likedRestaurantRepository;

    private final String currentDate;
    private Map<String, String> infoList;


    // Constructor
    public DetailRestaurantViewModel() {
        userRepository = UserRepository.getInstance();
        restaurantRepository = RestaurantRepository.getInstance();
        likedRestaurantRepository = LikedRestaurantRepository.getInstance();

        currentDate = DataProcessingUtils.getCurrentDate();
        infoList = new HashMap<>();
    }


    /************
     * LiveData *
     ************/

    public MutableLiveData<RestaurantWithDistance> getRestaurantDetailsMutableLiveData() {
        return restaurantRepository.getRestaurantDetailsMutableLiveData();
    }

    public MutableLiveData<List<User>> getWorkmatesMutableLiveData() {
        return userRepository.getWorkmatesMutableLiveData();
    }

    public MutableLiveData<List<LikedRestaurant>> getLikedRestaurantsMutableLiveData() {
        return likedRestaurantRepository.getLikedRestaurantsMutableLiveData();
    }


    /***********
     * Methods *
     ***********/

    // Fetchers (using Maps API)

    public void fetchRestaurantDetails(RestaurantWithDistance nearbyRestaurant, String apiKey) {
        restaurantRepository.fetchRestaurantDetails(nearbyRestaurant, apiKey);
    }


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

    public boolean checkCurrentUserSelection(String rId) {
        User currentUser = getCurrentUser();
        String selectionId = currentUser.getSelectionId();
        String selectionDate = currentUser.getSelectionDate();
        return (rId.equals(selectionId)) && (currentDate.equals(selectionDate));
    }

    public boolean checkCurrentUserLikes(String rId, List<LikedRestaurant> likedRestaurants) {
        String uId = getCurrentUser().getUid();
        boolean isLiked = false;
        if (likedRestaurants != null) {
            for (LikedRestaurant likedRestaurant : likedRestaurants) {
                if (Objects.equals(rId+uId, likedRestaurant.getId())) {
                    isLiked = true;
                    break;
                }
            }
        }
        return isLiked;
    }

    public String getOpeningInformation(RestaurantWithDistance restaurant) {
        // Built restaurant opening hours information to display
        String openingInformation = "";
        if (restaurant.getOpeningHours() != null) {
            // Possibility of several opening and closing periods in a day
            /** Information must be either 3 char (code) or 7 char (code+schedule) length */
            boolean openNow;
            long currentDayOfWeek = DataProcessingUtils.getCurrentDayOfWeek();
            String currentTime = DataProcessingUtils.getCurrentTime();

            // Get the list of opening periods
            List<Period> periods = restaurant.getOpeningHours().getPeriods();
            if (periods != null) {
                // There is at least one period
                List<Period> todayPeriods = new ArrayList<>();
                if (periods.size() == 1
                        && periods.get(0).getClose() == null
                        && periods.get(0).getOpen().getTime().equals("0000")) {
                    // If there is only one period, and it is open all week
                    openingInformation = infoList.get("OP*");                           // Open 24/7
                } else {
                    /* There is at least one period, so, we get details for each period...
                       ...and we get today's periods */
                    for (Period period : periods) {
                        // If period day matches with current day, add period to today periods list
                        if (period.getOpen().getDay() == currentDayOfWeek) todayPeriods.add(period);
                    }

                    // Analyze today's periods
                    if (todayPeriods.size() == 0) {
                        // There is no period for today, so it is closed
                        openingInformation = infoList.get("CLO");                          // Closed
                    } else if (todayPeriods.size() == 1
                            && todayPeriods.get(0).getOpen().getTime().equals("0000")
                            && todayPeriods.get(0).getClose() != null
                            && todayPeriods.get(0).getClose().getTime().equals("0000")
                            && todayPeriods.get(0).getClose().getDay() == currentDayOfWeek + 1) {
                        // If there is only one period for today, so it is open all day
                        openingInformation = infoList.get("OPD");                  // Open H24 today
                    } else {
                        // If there is at least one period for today : ...

                        // Sort today periods list by ascending opening time
                        DataProcessingUtils.sortByAscendingOpeningTime(todayPeriods);
                        // Calculate if the restaurant is currently open or closed
                        Period todayLastPeriod = todayPeriods.get(todayPeriods.size()-1);
                        openNow = false;
                        for (Period period : todayPeriods) {
                            if ((currentTime.compareTo(period.getOpen().getTime()) >= 0
                                    && currentTime.compareTo(period.getClose().getTime()) <= 0)) {
                                openNow = true;
                            }
                        }
                        if (currentTime.compareTo(todayLastPeriod.getOpen().getTime()) >= 0
                                && todayLastPeriod.getClose().getDay() != todayLastPeriod.getOpen().getDay()) {
                            openNow = true;
                        }

                        /* Determine opening information according to
                        whether the restaurant is currently open or closed */
                        if (openNow) {
                            // It is currently open
                            for (Period period : todayPeriods) {
                                String schedule = period.getClose().getTime();
                                if (currentTime.compareTo(schedule) < 0 ) {
                                    // Closing today at...
                                    openingInformation = infoList.get("OPU") + schedule; // Open until...
                                    break;
                                }
                            }
                            /* Current time doesn't match periods above...
                               ...so we check if last period ends after midnight */
                            if (openingInformation.isEmpty()) {
                                if (todayLastPeriod.getClose().getDay() != todayLastPeriod.getOpen().getDay()) {
                                    // Closing after midnight (last period schedule)
                                    String schedule = todayLastPeriod.getClose().getTime();
                                    openingInformation = infoList.get("OPU") + schedule; // Open until...
                                } else {
                                    // Unexpected case... A problem occurs somewhere !
                                    openingInformation = infoList.get("???"); // Unknown opening hours
                                    Log.w("DataProcessingUtils",
                                            "A problem has occurred when trying to retrieve opening information");
                                }
                            }
                        } else {
                            // It is currently closed
                            for (Period period : todayPeriods) {
                                String schedule = period.getOpen().getTime();
                                if (currentTime.compareTo(schedule) < 0 ) {
                                    // Opening today at...
                                    openingInformation = infoList.get("OPA") + schedule; // Open at...
                                    break;
                                }
                            }
                            if (openingInformation.isEmpty()) {
                                openingInformation = infoList.get("CLO");                  // Closed
                            }
                        }
                    }
                }
            } else {
                // No information about opening hours (periods is null)
                openingInformation = infoList.get("???");                   // Unknown opening hours
            }
        } else {
            // No information about opening hours (openingHours is null)
            openingInformation = infoList.get("???");                       // Unknown opening hours
        }

        return openingInformation;
    }


    // Getters

    public User getCurrentUser() {
        return userRepository.getCurrentUser();
    }

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

    public Map<String, String> getInfoList() {
        return infoList;
    }


    // Setters

    public void setInfoList(Map<String, String> map) {
        infoList.clear();
        infoList.putAll(map);
    }

}
