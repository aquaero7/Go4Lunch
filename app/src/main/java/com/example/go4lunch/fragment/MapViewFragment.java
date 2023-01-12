package com.example.go4lunch.fragment;


import static android.content.Context.LOCATION_SERVICE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.api.GmapsApiClient;
import com.example.go4lunch.api.GmapsRestaurantDetailsPojo;
import com.example.go4lunch.api.GmapsRestaurantPojo;
import com.example.go4lunch.databinding.FragmentMapViewBinding;
import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.Geometry;
import com.example.go4lunch.model.api.OpeningHours;
import com.example.go4lunch.model.api.Photo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapViewFragment extends Fragment {

    private FragmentMapViewBinding binding;
    private SupportMapFragment mapFragment;
    private LocationManager mLocationManager;
    private GoogleMap mGoogleMap;
    private LatLng home;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private double latitude;
    private double longitude;
    private boolean locationPermissionsGranted = false;
    private static final double DEF_LATITUDE = 48.7258;//VLB  // 48.8566;//Paris 48.7258;//VLB 43.0931;//SFLP 48.5959;//SLT
    private static final double DEF_LONGITUDE = 2.1252;//VLB  //  2.3522;//Paris  2.1252;//VLB  5.8392;//SFLP  2.5810;//SLT
    private static final int DEFAULT_ZOOM = 15;
    private final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private ActivityResultLauncher<String[]> requestPermissionsLauncher;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public MapViewFragment() {
    }

    // Factory method to create a new instance of this fragment
    public static MapViewFragment newInstance() {
        return (new MapViewFragment());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Set the layout file as the content view.
        binding = FragmentMapViewBinding.inflate(inflater, container, false);

        // Require latest version of map renderer
        // getLatestRenderer();    // TODO : Not working : Legacy version is used anyway !

        // Initialize SDK Places
        Places.initialize(requireContext(), getString(R.string.MAPS_API_KEY));
        // Create a new PlacesClient instance
        placesClient = Places.createClient(requireContext());
        // Create a new FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Register permissions callback
        registerPermissionsCallback();

        // Check permissions
        checkPermissions();
        // Initialize and load the map
        /** SOLUTION 1. Doesn't focus on current location at first display */   getDeviceLocation();
        /** SOLUTION 2. Doesn't focus on current location at first display */   // requestDeviceLocation();
        /** SOLUTION 3. Doesn't focus on current location at first display */   // startLocationUpdates();
        /** SOLUTION 4. Focus ok on current location at first display */        // getUpdatedLocation();
        getRestaurantsFromApi();
        loadMap();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get a handle to the fragment and register the callback.
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

    }

    @Override
    public void onResume() {
        super.onResume();
        // Setup toolbar title (Activity title)
        requireActivity().setTitle(R.string.mapView_toolbar_title);
        // Check permissions
        checkPermissions();
        // Initialize and load the map
        /** SOLUTION 1. Doesn't focus on current location at first display */   getDeviceLocation();
        /** SOLUTION 2. Doesn't focus on current location at first display */   // requestDeviceLocation();
        /** SOLUTION 3. Doesn't focus on current location at first display */   // startLocationUpdates();
        /** SOLUTION 4. Focus ok on current location at first display */        // getUpdatedLocation();
        getRestaurantsFromApi();
        loadMap();

    }

    @Override
    public void onPause() {
        super.onPause();

        // Disconnect listener from system updates
        /** USED WITH SOLUTION 2 */ // if (fusedLocationProviderClient != null && locationCallback != null)
                                        // fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        /** USED WITH SOLUTION 4 */ // if (mLocationManager != null) mLocationManager.removeUpdates(locationListener);
    }


    private void getLatestRenderer() {
        MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST, renderer -> {
            switch (renderer) {
                case LATEST:
                    Log.w("MapsDemo", "The latest version of the renderer is used.");
                    break;
                case LEGACY:
                    Log.w("MapsDemo", "The legacy version of the renderer is used.");
                    break;
            }
        });
    }

    private void registerPermissionsCallback() {
        ActivityResultContracts.RequestMultiplePermissions permissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        requestPermissionsLauncher = registerForActivityResult(permissionsContract, result -> {
            Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
            Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION,false);
            if (fineLocationGranted != null && fineLocationGranted) {
                locationPermissionsGranted = true;
                Log.w("ActivityResultLauncher", "Fine location permission was granted");
            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                locationPermissionsGranted = true;
                Log.w("ActivityResultLauncher", "Only coarse location permission was granted");
            } else {
                // locationPermissionsGranted = false;
                Log.w("ActivityResultLauncher", "No location permission was granted");
            }
        });
    }

    private void checkPermissions() {
        // Check and request permissions
        if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            && (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Log.w("checkPermissions", "Permissions not granted");
            // The registered ActivityResultCallback gets the result of this(these) request(s).
            requestPermissionsLauncher.launch(PERMISSIONS);
        } else {
            Log.w("checkPermissions", "Permissions granted");
            locationPermissionsGranted = true;
        }
    }

    // USED WITH SOLUTION 1 :
    @SuppressWarnings("MissingPermission")
    // Permissions already checked in checkPermissionsAndLoadMap() method, called in onResume() method
    private void getDeviceLocation() {
        try {
            if (locationPermissionsGranted) {
                // Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                Task<Location> locationResult = fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null);
                locationResult.addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            latitude = lastKnownLocation.getLatitude();
                            longitude = lastKnownLocation.getLongitude();
                        } else {
                            latitude = DEF_LATITUDE;
                            longitude = DEF_LONGITUDE;
                            Toast.makeText(requireActivity(), R.string.info_no_current_location, Toast.LENGTH_SHORT).show();
                            Log.w("getDeviceLocation", "Exception: %s", task.getException());
                        }
                    }
                    else {
                        Log.w("getDeviceLocation", "Exception: %s", task.getException());
                    }
                });
            } else {
                latitude = DEF_LATITUDE;
                longitude = DEF_LONGITUDE;
                Toast.makeText(requireActivity(), R.string.info_no_permission, Toast.LENGTH_SHORT).show();
                Log.w("getDeviceLocation", "Permissions not granted");
            }
        } catch (SecurityException e) {
            Log.w("Exception: %s", e.getMessage(), e);
        }
    }

    @SuppressWarnings("MissingPermission")
    // Permissions already checked in checkPermissionsAndLoadMap() method, called in onResume() method
    private void loadMap() {
        if (mapFragment != null) {
            /* Manipulates the map once available.
                This callback is triggered when the map is ready to be used.
                This is where we can add markers or lines, add listeners or move the camera. */
            mapFragment.getMapAsync(googleMap -> {

                // SET POSITION ON MAP
                mGoogleMap = googleMap;
                home = new LatLng(latitude, longitude);

                // CUSTOMIZE MAP
                mGoogleMap.setMyLocationEnabled(locationPermissionsGranted);

                // Set camera position
                // mGoogleMap.moveCamera(CameraUpdateFactory.zoomBy(DEFAULT_ZOOM));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, DEFAULT_ZOOM));

                // Other settings
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(48.7270, 2.1200))
                        .title("Marker in VLB"));   // TODO : Test to be deleted
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(43.0900, 5.8400))
                        .title("Marker in SFLP"));  // TODO : Test to be deleted



            });
        }
    }

    // Get restaurants list from API
    private void getRestaurantsFromApi() {
        if (locationPermissionsGranted) {
            // Call Place Nearby Search API
            Call<GmapsRestaurantPojo> call1 = GmapsApiClient.getApiClient().getPlaces("restaurant", latitude + "," + longitude, 1000, getString(R.string.MAPS_API_KEY));
            call1.enqueue(new Callback<GmapsRestaurantPojo>() {
                @Override
                public void onResponse(@NonNull Call<GmapsRestaurantPojo> call1, @NonNull Response<GmapsRestaurantPojo> response1) {
                    GmapsRestaurantPojo nearPlaces = response1.body();
                    List<Restaurant> restaurantsList = nearPlaces.getNearRestaurants();

                    // For each restaurant, get basic information and ask for detailed information
                    if (restaurantsList.size() != 0) {
                        Log.w("MAPViewFragment", "Nearby restaurants list not empty");
                        for (Restaurant nearbyRestaurant : restaurantsList) {
                            getRestaurantDetailsFromApi(nearbyRestaurant);
                        }
                    } else {
                        Log.w("MAPViewFragment", "Empty nearby restaurants list");
                    }


                }

                @Override
                public void onFailure(@NonNull Call<GmapsRestaurantPojo> call1, @NonNull Throwable t) {
                    Toast.makeText(requireContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    Log.w("MAPViewFragment", t.getMessage(), t);
                }
            });
        }
    }

    private void getRestaurantDetailsFromApi(Restaurant nearbyRestaurant) {

        String id = nearbyRestaurant.getId();
        String name = nearbyRestaurant.getName();
        double rating = nearbyRestaurant.getRating();
        OpeningHours openingHours = nearbyRestaurant.getOpeningHours();
        List<Photo> photos = nearbyRestaurant.getPhotos();
        /*
        if (photos.size() != 0) {
            String imageUrl = photos.get(0).getPhotoUrl(getString(R.string.MAPS_API_KEY));
        }
        */
        Geometry geometry = nearbyRestaurant.getGeometry();
        // double latitude = geometry.getLocation().getLat();
        // double longitude = geometry.getLocation().getLng();

        int distance = 0;   // TODO : To be calculated ?

        // Call Place Details API
        Call<GmapsRestaurantDetailsPojo> call2 = GmapsApiClient.getApiClient().getPlaceDetails(id,
                "formatted_address,formatted_phone_number,website",
                getString(R.string.MAPS_API_KEY)
        );
        call2.enqueue(new Callback<GmapsRestaurantDetailsPojo>() {
            @Override
            public void onResponse(@NonNull Call<GmapsRestaurantDetailsPojo> call2, @NonNull Response<GmapsRestaurantDetailsPojo> response2) {
                // Gson gson = new Gson();
                // String res = gson.toJson(response2.body());
                GmapsRestaurantDetailsPojo placeDetails = response2.body();
                Restaurant restaurantDetails = placeDetails.getRestaurantDetails();

                String nationality = "";    // TODO : Where to get this information ?
                String address = restaurantDetails.getAddress();
                String phoneNumber = restaurantDetails.getPhoneNumber();
                String website = restaurantDetails.getWebsite();

                // Create ou update restaurant in Firebase
                RestaurantManager.getInstance().createRestaurant(id, name, distance, photos,
                        nationality, address, rating, openingHours, phoneNumber, website, geometry);

            }

            @Override
            public void onFailure(@NonNull Call<GmapsRestaurantDetailsPojo> call2, @NonNull Throwable t) {
                Toast.makeText(requireContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
                Log.w("MAPViewFragment", t.getMessage(), t);
            }
        });



    }













//  TODO : TO ARCHIVE : OTHER SOLUTIONS AVAILABLE TO GET AND UPDATE CURRENT LOCATION................

    // USED WITH SOLUTION 2 ////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("MissingPermission")
    private void requestDeviceLocation() {
        if(locationPermissionsGranted) {
            locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(500)
                    .setMaxUpdateDelayMillis(1000)
                    .build();

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                    super.onLocationAvailability(locationAvailability);
                }

                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (!locationResult.getLocations().isEmpty()) {
                        latitude = Objects.requireNonNull(locationResult.getLastLocation()).getLatitude();
                        longitude = Objects.requireNonNull(locationResult.getLastLocation()).getLongitude();
                    } else {
                        latitude = DEF_LATITUDE;
                        longitude = DEF_LONGITUDE;
                    }
                }
            };
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            latitude = DEF_LATITUDE;
            longitude = DEF_LONGITUDE;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // USED WITH SOLUTION 3 ////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if (locationPermissionsGranted) {
            // Create the location request to start receiving updates
            locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(500)
                    .setMaxUpdateDelayMillis(1000)
                    .build();

            // Create LocationSettingsRequest object using location request
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(locationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();

            // Check whether location settings are satisfied
            // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
            SettingsClient settingsClient = LocationServices.getSettingsClient(requireActivity());
            settingsClient.checkLocationSettings(locationSettingsRequest);

            // new Google API SDK v11 uses getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    // do work here
                    onLocationChanged(Objects.requireNonNull(locationResult.getLastLocation()));
                }
            },
            Looper.myLooper());
        } else {
            latitude = DEF_LATITUDE;
            longitude = DEF_LONGITUDE;
        }
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        // You can now create a LatLng Object for use with maps
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // USED WITH SOLUTION 4 (OLDER SOLUTION) ///////////////////////////////////////////////////////

    // Listen to locations changes and get current position
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            // Update location metrics
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    };

    @SuppressWarnings("MissingPermission")
    // Permissions already checked in checkPermissionsAndLoadMap() method, called in onResume() method
    private void getUpdatedLocation() {
        if (locationPermissionsGranted) {
            // Get updated location from system and send it to listeners
            mLocationManager = (LocationManager) requireActivity().getSystemService(LOCATION_SERVICE);
            String bestProvider = null;
            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                bestProvider = LocationManager.NETWORK_PROVIDER;
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, locationListener);
            }
            if (mLocationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                bestProvider = LocationManager.PASSIVE_PROVIDER;
                mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 0, locationListener);
            }
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                bestProvider = LocationManager.GPS_PROVIDER;
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
            }

            latitude = mLocationManager.getLastKnownLocation(bestProvider).getLatitude();
            longitude = mLocationManager.getLastKnownLocation(bestProvider).getLongitude();
            Log.w("getDeviceLocation", "Best Provider found: " + bestProvider);

        } else {
            latitude = DEF_LATITUDE;
            longitude = DEF_LONGITUDE;
            Toast.makeText(requireActivity(), R.string.info_no_permission, Toast.LENGTH_SHORT).show();
            Log.w("getDeviceLocation", "Permissions not granted");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

}