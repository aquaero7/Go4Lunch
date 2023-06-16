package com.example.go4lunch.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.go4lunch.R;
import com.example.go4lunch.api.GmapsApiClient;
import com.example.go4lunch.api.GmapsRestaurantDetailsPojo;
import com.example.go4lunch.api.GmapsRestaurantPojo;
import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.api.Geometry;
import com.example.go4lunch.model.api.OpeningHours;
import com.example.go4lunch.model.api.Photo;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.utils.MapsApisUtils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantViewModel extends ViewModel {

    private MutableLiveData<List<Restaurant>> mMutableLiveData;
    private LatLng home;
    private List<Restaurant> restaurantsList = new ArrayList<>();
    private List<Restaurant> nearbyRestaurantsList = new ArrayList<>();

    public RestaurantViewModel() {
        // mRestaurantRepository = new RestaurantRepository();
        mMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<Restaurant>> getMutableLiveData() {
        return mMutableLiveData;
    }

    private void getRestaurantDetailsFromApi(Restaurant nearbyRestaurant, String apiKey, Context context) {

        String rId = nearbyRestaurant.getRid();
        String name = nearbyRestaurant.getName();
        double rating = nearbyRestaurant.getRating();
        OpeningHours openingHours = nearbyRestaurant.getOpeningHours(); // Can be commented if openingHours come from details api
        List<Photo> photos = nearbyRestaurant.getPhotos();
        Geometry geometry = nearbyRestaurant.getGeometry();

        // Call Place Details API
        Call<GmapsRestaurantDetailsPojo> call2 = GmapsApiClient.getApiClient().getPlaceDetails(rId,
                "formatted_address,formatted_phone_number,website,opening_hours",
                apiKey
        );
        call2.enqueue(new Callback<GmapsRestaurantDetailsPojo>() {
            @Override
            public void onResponse(@NonNull Call<GmapsRestaurantDetailsPojo> call2, @NonNull Response<GmapsRestaurantDetailsPojo> response2) {
                GmapsRestaurantDetailsPojo placeDetails = response2.body();
                Restaurant restaurantDetails = placeDetails.getRestaurantDetails();

                String address = restaurantDetails.getAddress();
                String phoneNumber = restaurantDetails.getPhoneNumber();
                String website = restaurantDetails.getWebsite();
                OpeningHours openingHours = restaurantDetails.getOpeningHours();    // Can be commented to make openingHours come from nearby api

                /** Add restaurant to current restaurants list */
                restaurantsList.add(new Restaurant(rId, name, photos, address, rating, openingHours,
                        phoneNumber, website, geometry));

                /** Create or update restaurant in Firebase */
                RestaurantManager.getInstance().createRestaurant(rId, name, photos, address, rating,
                        openingHours, phoneNumber, website, geometry);
            }

            @Override
            public void onFailure(@NonNull Call<GmapsRestaurantDetailsPojo> call2, @NonNull Throwable t) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                Log.w("RestaurantViewModel", t.getMessage(), t);
            }
        });
    }

    private List<Restaurant> getRestaurantsFromApi(Context context, LatLng home, String apiKey) {
        // Call Place Nearby Search API
        Call<GmapsRestaurantPojo> call1 = GmapsApiClient.getApiClient()
                .getPlaces("restaurant", home.latitude + "," + home.longitude, Integer.parseInt(MapsApisUtils.getSearchRadius())*1000, apiKey);
        call1.enqueue(new Callback<GmapsRestaurantPojo>() {
            @Override
            public void onResponse(@NonNull Call<GmapsRestaurantPojo> call1, @NonNull Response<GmapsRestaurantPojo> response1) {
                GmapsRestaurantPojo nearPlaces = response1.body();
                List<Restaurant> nearbyRestaurants = nearPlaces.getNearRestaurants();

                // For each restaurant, get basic information ask for detailed information and add restaurant to the list
                if (nearbyRestaurants != null) {
                    Log.w("RestaurantViewModel", "Nearby restaurants list not empty");
                    restaurantsList.clear();
                    for (Restaurant nearbyRestaurant : nearbyRestaurants) {
                        getRestaurantDetailsFromApi(nearbyRestaurant, apiKey, context);
                    }
                } else {
                    Log.w("RestaurantViewModel", "Empty nearby restaurants list");
                }
                //
            }

            @Override
            public void onFailure(@NonNull Call<GmapsRestaurantPojo> call1, @NonNull Throwable t) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                Log.w("RestaurantViewModel", t.getMessage(), t);
            }
        });
        return restaurantsList;
    }


    // Get restaurants list and populate the LiveData
    public void fetchRestaurants(Activity activity, String apiKey) {
        LocationViewModel locationViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(LocationViewModel.class);
        locationViewModel.getMutableLiveData().observe((LifecycleOwner) activity, latLng -> {
            home = latLng;
            if (home != null) {
                // Get nearby restaurants list from API
                nearbyRestaurantsList = getRestaurantsFromApi(activity, home, apiKey);
                // Populate the LiveData
                mMutableLiveData.setValue(nearbyRestaurantsList);
            }
        });
    }

}
