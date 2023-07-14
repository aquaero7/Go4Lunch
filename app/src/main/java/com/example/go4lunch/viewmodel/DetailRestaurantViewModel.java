package com.example.go4lunch.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.R;
import com.example.go4lunch.model.api.model.Period;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.repository.LikedRestaurantRepository;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.model.model.LikedRestaurant;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailRestaurantViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final LikedRestaurantRepository likedRestaurantRepository;


    // Constructor
    public DetailRestaurantViewModel() {
        userRepository = UserRepository.getInstance();
        restaurantRepository = RestaurantRepository.getInstance();
        likedRestaurantRepository = LikedRestaurantRepository.getInstance();
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

    public String updateLikedRestaurant(String rId) {
        if (!likedRestaurantRepository.isRestaurantLiked()) {
            createLikedRestaurant(userRepository.getFbCurrentUserId(), rId);
            return MainApplication.getInstance().getString(R.string.btn_like_checked);
        } else {
            deleteLikedRestaurant(userRepository.getFbCurrentUserId(), rId);
            return MainApplication.getInstance().getString(R.string.btn_like_unchecked);
        }
    }

    public void createLikedRestaurant(String uId, String rId) {
        likedRestaurantRepository.createLikedRestaurant(rId+uId, rId, uId);     // Document in database
        likedRestaurantRepository.updateLikedRestaurants(rId+uId, rId, uId);    // Local list
    }

    public void deleteLikedRestaurant(String uId, String rId) {
        likedRestaurantRepository.deleteLikedRestaurant(rId+uId);   // Document in database
        likedRestaurantRepository.updateLikedRestaurants(rId+uId);  // Local list
    }

    public String updateSelection(String rId, String rName, String rAddress) {
        if (!restaurantRepository.isRestaurantSelected()) {
            // Add selected restaurant ID to current user
            createSelection(rId, rName, rAddress);
            return MainApplication.getInstance().getString(R.string.fab_checked);
        } else {
            // Remove selected restaurant ID from current user
            deleteSelection();
            return MainApplication.getInstance().getString(R.string.fab_unchecked);
        }
    }

    public void createSelection(String rId, String rName, String rAddress) {
        final String currentDate = Utils.getCurrentDate();
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

    public boolean checkCurrentUserSelection(List<User> selectors) {
        restaurantRepository.setRestaurantSelected(false);
        for (User user : selectors) {
            if (Objects.equals(userRepository.getFbCurrentUserId(), user.getUid())) {
                restaurantRepository.setRestaurantSelected(true);
                break;
            }
        }
        return restaurantRepository.isRestaurantSelected();
    }

    public boolean checkCurrentUserLikes(String rId, List<LikedRestaurant> likedRestaurants) {
        String uId = userRepository.getFbCurrentUserId();
        likedRestaurantRepository.setRestaurantLiked(false);
        if (likedRestaurants != null) {
            for (LikedRestaurant likedRestaurant : likedRestaurants) {
                if (Objects.equals(rId+uId, likedRestaurant.getId())) {
                    likedRestaurantRepository.setRestaurantLiked(true);
                    break;
                }
            }
        }
        return likedRestaurantRepository.isRestaurantLiked();
    }

    public void sortByName(List<User> workmatesList) {
        userRepository.sortByName(workmatesList);
    }

    public void sortByAscendingOpeningTime(List<Period> periods) {
        restaurantRepository.sortByAscendingOpeningTime(periods);
    }

    public void sortByDescendingOpeningTime(List<Period> periods) {
        restaurantRepository.sortByDescendingOpeningTime(periods);
    }


    // Getters

    public String getOpeningInformation(RestaurantWithDistance restaurant) {
        Application application = MainApplication.getInstance(); // To get access to 'getString()'

        // Built restaurant opening hours information to display
        String openingInformation = "";
        if (restaurant.getOpeningHours() != null) {
            // Possibility of several opening and closing periods in a day
            /** Information must be either 3 char (code) or 7 char (code+schedule) length */
            boolean openNow;
            long currentDayOfWeek = Utils.getCurrentDayOfWeek();
            String currentTime = Utils.getCurrentTime();

            // Get the list of opening periods
            List<Period> periods = restaurant.getOpeningHours().getPeriods();
            if (periods != null) {
                // There is at least one period
                List<Period> todayPeriods = new ArrayList<>();
                if (periods.size() == 1
                        && periods.get(0).getClose() == null
                        && periods.get(0).getOpen().getTime().equals("0000")) {
                    // If there is only one period, and it is open all week
                    openingInformation = application.getString(R.string.status_open247); // Open 24/7
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
                        openingInformation = application.getString(R.string.status_closed); // Closed
                    } else if (todayPeriods.size() == 1
                            && todayPeriods.get(0).getOpen().getTime().equals("0000")
                            && todayPeriods.get(0).getClose() != null
                            && todayPeriods.get(0).getClose().getTime().equals("0000")
                            && todayPeriods.get(0).getClose().getDay() == currentDayOfWeek + 1) {
                        // If there is only one period for today, so it is open all day
                        openingInformation = application.getString(R.string.status_open24); // Open H24 today
                    } else {
                        // If there is at least one period for today : ...

                        // Sort today periods list by ascending opening time
                        sortByAscendingOpeningTime(todayPeriods);
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
                                schedule = schedule.substring(0,2) + "h" + schedule.substring(2);
                                if (currentTime.compareTo(schedule) < 0 ) {
                                    // Closing today at...
                                    openingInformation = application.getString(R.string.status_open_until) + schedule; // Open until...
                                    break;
                                }
                            }
                            /* Current time doesn't match periods above...
                               ...so we check if last period ends after midnight */
                            if (openingInformation.isEmpty()) {
                                if (todayLastPeriod.getClose().getDay() != todayLastPeriod.getOpen().getDay()) {
                                    // Closing after midnight (last period schedule)
                                    String schedule = todayLastPeriod.getClose().getTime();
                                    schedule = schedule.substring(0,2) + "h" + schedule.substring(2);
                                    openingInformation = application.getString(R.string.status_open_until) + schedule; // Open until...
                                } else {
                                    // Unexpected case... A problem occurs somewhere !
                                    openingInformation = application.getString(R.string.status_unknown); // Unknown opening hours
                                    Log.w("Utils",
                                            "A problem has occurred when trying to retrieve opening information");
                                }
                            }
                        } else {
                            // It is currently closed
                            for (Period period : todayPeriods) {
                                String schedule = period.getOpen().getTime();
                                schedule = schedule.substring(0,2) + "h" + schedule.substring(2);
                                if (currentTime.compareTo(schedule) < 0 ) {
                                    // Opening today at...
                                    openingInformation = application.getString(R.string.status_open_at) + schedule; // Open at...
                                    break;
                                }
                            }
                            if (openingInformation.isEmpty()) {
                                openingInformation = application.getString(R.string.status_closed); // Closed
                            }
                        }
                    }
                }
            } else {
                // No information about opening hours (periods is null)
                openingInformation = application.getString(R.string.status_unknown); // Unknown opening hours
            }
        } else {
            // No information about opening hours (openingHours is null)
            openingInformation = application.getString(R.string.status_unknown); // Unknown opening hours
        }

        return openingInformation;
    }

    public RestaurantWithDistance getRestaurant() {
        return restaurantRepository.getRestaurant();
    }

    public List<User> getSelectors(String rId, List<User> workmates) {
        List<User> selectors = new ArrayList<>();
        // Check selected restaurant id and date
        for (User workmate : workmates) {
            boolean isSelector = (Objects.equals(rId, workmate.getSelectionId())
                    && Utils.getCurrentDate().equals(workmate.getSelectionDate()));
            if (isSelector) selectors.add(workmate);
        }
        sortByName(selectors);
        userRepository.setSelectors(selectors);
        return selectors;
    }

    public List<User> getSelectors() {
        return userRepository.getSelectors();
    }


    // Setters

    public void setRestaurant(RestaurantWithDistance restaurant) {
        restaurantRepository.setRestaurant(restaurant);
    }

}
