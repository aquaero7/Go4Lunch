package com.example.go4lunch.model.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.api.GmapsApiClient;
import com.example.go4lunch.model.api.GmapsApiInterface;
import com.example.go4lunch.model.api.GmapsRestaurantDetailsPojo;
import com.example.go4lunch.model.api.GmapsRestaurantPojo;
import com.example.go4lunch.model.api.model.Period;
import com.example.go4lunch.model.model.Restaurant;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.api.model.Geometry;
import com.example.go4lunch.model.api.model.OpeningHours;
import com.example.go4lunch.model.api.model.Photo;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    /** DEFAULT VALUES **/
    public static final String DEFAULT_RADIUS = "1"; // Distance in km
    /********************/

    private static volatile RestaurantRepository instance;
    private final MutableLiveData<List<RestaurantWithDistance>> restaurantsMutableLiveData;
    private final MutableLiveData<RestaurantWithDistance> restaurantDetailsMutableLiveData;
    private final List<Restaurant> restaurantsList;
    private List<RestaurantWithDistance> restaurantsListWithDistance;
    private final List<RestaurantWithDistance> restaurantsToDisplay;
    private RestaurantWithDistance restaurant;
    private boolean restaurantIsSelected;
    private final GmapsApiInterface apiClient;

    private RestaurantRepository() {
        apiClient = GmapsApiClient.getApiClient();

        restaurantsMutableLiveData = new MutableLiveData<>();
        restaurantDetailsMutableLiveData = new MutableLiveData<>();

        restaurantsList = new ArrayList<>();
        restaurantsListWithDistance = new ArrayList<>();
        restaurantsToDisplay = new ArrayList<>();
    }

    public static RestaurantRepository getInstance() {
        RestaurantRepository result = instance;
        if (result != null) {
            return result;
        } else {
            instance = new RestaurantRepository();
            return instance;
        }
    }

    /** For test use only : GmapsApiInterface dependency injection and new instance factory *******/
    private RestaurantRepository(GmapsApiInterface apiClient) {
        this.apiClient = apiClient;

        restaurantsMutableLiveData = new MutableLiveData<>();
        restaurantDetailsMutableLiveData = new MutableLiveData<>();

        restaurantsList = new ArrayList<>();
        restaurantsListWithDistance = new ArrayList<>();
        restaurantsToDisplay = new ArrayList<>();
    }

    public static RestaurantRepository getNewInstance(GmapsApiInterface apiClient) {
        instance = new RestaurantRepository(apiClient);
        return instance;
    }
    /**********************************************************************************************/


    public void fetchRestaurants(LatLng home, String radius, String apiKey) {
        // Call Place Nearby Search API <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        Call<GmapsRestaurantPojo> call1 = apiClient.getPlaces(
                "restaurant", home.latitude + "," + home.longitude,
                Integer.parseInt(radius)*1000, apiKey);
        call1.enqueue(new Callback<GmapsRestaurantPojo>() {
            @Override
            public void onResponse(@NonNull Call<GmapsRestaurantPojo> call1, @NonNull Response<GmapsRestaurantPojo> response1) {
                GmapsRestaurantPojo nearPlaces = response1.body();
                List<Restaurant> nearbyRestaurants = Objects.requireNonNull(nearPlaces).getNearRestaurants();

                // For each restaurant, get basic information ask for detailed information and add restaurant to the list
                if (nearbyRestaurants != null) {
                    Log.w("RestaurantRepository", "Nearby restaurants list not empty");
                    restaurantsList.clear();
                    for (Restaurant nearbyRestaurant : nearbyRestaurants) {
                        String rId = nearbyRestaurant.getRid();
                        String name = nearbyRestaurant.getName();
                        String address = nearbyRestaurant.getAddress(); // Not implemented by this API
                        String phoneNumber = nearbyRestaurant.getPhoneNumber(); // Not implemented by this API
                        String website = nearbyRestaurant.getWebsite(); // Not implemented by this API
                        double rating = nearbyRestaurant.getRating();
                        OpeningHours openingHours = nearbyRestaurant.getOpeningHours();
                        List<Photo> photos = nearbyRestaurant.getPhotos();
                        Geometry geometry = nearbyRestaurant.getGeometry();

                        // Add restaurant to current restaurants list
                        restaurantsList.add(new Restaurant(rId, name, photos, address, rating, openingHours,
                                phoneNumber, website, geometry));
                    }

                    restaurantsListWithDistance = updateRestaurantsListWithDistances(restaurantsList, home);
                    sortByDistanceAndName(restaurantsListWithDistance);
                    // Populate the LiveData
                    setRestaurantsMutableLiveData(restaurantsListWithDistance);
                } else {
                    Log.w("RestaurantRepository", "Empty nearby restaurants list");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GmapsRestaurantPojo> call1, @NonNull Throwable t) {
                Log.w("RestaurantRepository", t.getMessage(), t);
            }
        });
    }

    public void fetchRestaurantDetails(RestaurantWithDistance nearbyRestaurant, String apiKey) {

        // Call Place Details API <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        Call<GmapsRestaurantDetailsPojo> call2 = apiClient.getPlaceDetails(
                nearbyRestaurant.getRid(),
                "formatted_address,formatted_phone_number,website,opening_hours",
                apiKey);
        call2.enqueue(new Callback<GmapsRestaurantDetailsPojo>() {
            @Override
            public void onResponse(@NonNull Call<GmapsRestaurantDetailsPojo> call2,
                                   @NonNull Response<GmapsRestaurantDetailsPojo> response2) {
                GmapsRestaurantDetailsPojo placeDetails = response2.body();
                Restaurant restaurantDetails = Objects.requireNonNull(placeDetails).getRestaurantDetails();

                // Update fields with detail information
                nearbyRestaurant.setAddress(restaurantDetails.getAddress());
                nearbyRestaurant.setOpeningHours(restaurantDetails.getOpeningHours());  // Updated with full information
                nearbyRestaurant.setPhoneNumber(restaurantDetails.getPhoneNumber());
                nearbyRestaurant.setWebsite(restaurantDetails.getWebsite());

                // Populate the LiveData
                setRestaurantDetailsMutableLiveData(nearbyRestaurant);
            }

            @Override
            public void onFailure(@NonNull Call<GmapsRestaurantDetailsPojo> call2, @NonNull Throwable t) {
                Log.w("RestaurantRepository", t.getMessage(), t);
            }
        });
    }

    public List<RestaurantWithDistance> updateRestaurantsListWithDistances(List<Restaurant> restaurantsList, LatLng home) {
        List<RestaurantWithDistance> restaurants = new ArrayList<>();
        for (Restaurant restaurant : restaurantsList) {
            int distance = calculateRestaurantDistance(restaurant, home);

            RestaurantWithDistance restaurantWithDistance
                    = new RestaurantWithDistance(restaurant.getRid(), restaurant.getName(),
                    restaurant.getPhotos(), restaurant.getAddress(), restaurant.getRating(),
                    restaurant.getOpeningHours(), restaurant.getPhoneNumber(),
                    restaurant.getWebsite(), restaurant.getGeometry(), distance);

            restaurants.add(restaurantWithDistance);
        }
        return restaurants;
    }

    public int calculateRestaurantDistance(Restaurant restaurant, LatLng currentLatLng) {
        double restaurantLat = restaurant.getGeometry().getLocation().getLat();
        double restaurantLng = restaurant.getGeometry().getLocation().getLng();
        LatLng restaurantLatLng = new LatLng(restaurantLat, restaurantLng);
        double distance = SphericalUtil.computeDistanceBetween(currentLatLng, restaurantLatLng);

        return (int) distance;  /** Distance in meters */
    }

    public void sortByDistanceAndName (List<RestaurantWithDistance> restaurantsWithDistance) {
        restaurantsWithDistance.sort(RestaurantWithDistance.comparatorName);
        restaurantsWithDistance.sort(RestaurantWithDistance.comparatorDistance);
    }

    public void sortByAscendingOpeningTime(List<Period> periods) {
        // Using Comparator
        periods.sort(Comparator.comparing(o -> o.getOpen().getTime()));
        // periods.sort(Comparator.comparing(o -> o.getOpen().getTime(), Comparator.naturalOrder()));

        // Or without using Comparator
        // periods.sort((o1, o2) -> o1.getOpen().getTime().compareTo(o2.getOpen().getTime()));
    }

    public void sortByDescendingOpeningTime(List<Period> periods) {
        // Using Comparator
        periods.sort(Comparator.comparing(o -> o.getOpen().getTime(), Comparator.reverseOrder()));

        // Or without using Comparator
        // periods.sort((o1, o2) -> o2.getOpen().getTime().compareTo(o1.getOpen().getTime()));
    }

    public MutableLiveData<List<RestaurantWithDistance>> getRestaurantsMutableLiveData() {
        return restaurantsMutableLiveData;
    }

    public void setRestaurantsMutableLiveData(List<RestaurantWithDistance> restaurants) {
        restaurantsMutableLiveData.setValue(restaurants);
    }

    public MutableLiveData<RestaurantWithDistance> getRestaurantDetailsMutableLiveData() {
        return restaurantDetailsMutableLiveData;
    }

    public void setRestaurantDetailsMutableLiveData(RestaurantWithDistance restaurant) {
        restaurantDetailsMutableLiveData.setValue(restaurant);
    }

    public String getDefaultRadius() {
        return DEFAULT_RADIUS;
    }

    public void setRestaurant(RestaurantWithDistance mRestaurant) {
        restaurant = mRestaurant;
    }

    public RestaurantWithDistance getRestaurant() {
        return restaurant;
    }

    public void setRestaurantSelected(boolean selected) {
        restaurantIsSelected = selected;
    }

    public boolean isRestaurantSelected() {
        return restaurantIsSelected;
    }

    public List<RestaurantWithDistance> getRestaurants() {
        return restaurantsListWithDistance;
    }

    public List<RestaurantWithDistance> getRestaurantsToDisplay() {
        return restaurantsToDisplay;
    }

    public void setRestaurantsToDisplay(List<RestaurantWithDistance> restaurants) {
        restaurantsToDisplay.clear();
        restaurantsToDisplay.addAll(restaurants);
    }

}
