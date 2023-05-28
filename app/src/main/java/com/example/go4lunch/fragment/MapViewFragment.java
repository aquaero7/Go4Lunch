package com.example.go4lunch.fragment;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.activity.DetailRestaurantActivity;
import com.example.go4lunch.databinding.FragmentMapViewBinding;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.CalendarUtils;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.example.go4lunch.utilsforviews.EventListener;
import com.example.go4lunch.utils.FirestoreUtils;
import com.example.go4lunch.utils.MapsApisUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MapViewFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener,
        OnMapsSdkInitializedCallback {


    private FragmentMapViewBinding binding;
    private SupportMapFragment mapFragment;
    private CardView autocompleteCardView;
    private AutocompleteSupportFragment autocompleteFragment;

    private EventListener eventListener;

    private boolean locationPermissionsGranted;
    private boolean focusHome;
    private GoogleMap mGoogleMap;
    private LatLng home;
    private PlacesClient placesClient;

    private static final int DEFAULT_ZOOM = 17;
    private static final int RESTAURANT_ZOOM = 19;
    private List<Restaurant> restaurantsList = new ArrayList<>();
    private List<User> workmatesList;
    private int selectionsCount;
    private final String currentDate = CalendarUtils.getCurrentDate();

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

        // Require latest version of map renderer   // Not working : Legacy version is used anyway !
        MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST, this);

        // Initialize SDK Places for Autocomplete API
        Places.initialize(requireContext(), getString(R.string.MAPS_API_KEY));
        // Create a new PlacesClient instance or Autocomplete API
        placesClient = Places.createClient(requireContext());

        /** To use if menu is handled in fragment
         * Works with onCreateOptionsMenu() and onOptionsItemSelected() */
        setHasOptionsMenu(true);

        // Initialize CardView
        autocompleteCardView = binding.includedCardViewAutocomplete.cardViewAutocomplete;

        // Initialize AutocompleteSupportFragment
        // autocompleteFragment = (AutocompleteSupportFragment) getParentFragmentManager().findFragmentById(R.id.fragment_autocomplete);
        autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.fragment_autocomplete);
        MapsApisUtils.initializeAutocompleteSupportFragment(Objects.requireNonNull(autocompleteFragment));

        // return rootView;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get a handle to the fragment and register the callback.
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // Acquiring the map
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof EventListener) {
            eventListener = (EventListener) context;
        } else {
            Log.w("MapViewFragment", "EventListener error");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Setup toolbar title (Activity title)
        requireActivity().setTitle(R.string.mapView_toolbar_title);
        // Display map with or without home focus
        displayMap();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Disconnect listener from system updates
        /** USED WITH SOLUTION 2 */ // if (fusedLocationProviderClient != null && locationCallback != null)
                                        // fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        /** USED WITH SOLUTION 4 */ // if (mLocationManager != null) mLocationManager.removeUpdates(locationListener);
    }

    /** To use with setHasOptionsMenu(true), if menu is handled in fragment */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_main_menu, menu);
    }

    /** To use with setHasOptionsMenu(true), if menu is handled in fragment */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle actions on menu items
        switch (item.getItemId()) {
            case R.id.menu_activity_main_search:
                eventListener.toggleSearchViewVisibility();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapsSdkInitialized(@NonNull MapsInitializer.Renderer renderer) {
        switch (renderer) {
            case LATEST:
                Log.w("MapViewFragment", "The latest version of the renderer is used.");
                break;
            case LEGACY:
                Log.w("MapViewFragment", "The legacy version of the renderer is used.");
                break;
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        /** Manipulates the map once available.
         This callback is triggered when the map is ready to be used.
         This is where we can add markers or lines, add listeners or move the camera. */
        mGoogleMap = googleMap;
        // Initialize the map
        initializeMap();
        // Display map with or without home focus
        displayMap();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Request for home focus and default zoom
        focusHome = true;
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        // Refresh and display restaurant list
        restaurantsList = FirestoreUtils.getRestaurantsList();
        displayRestaurantsOnMap(restaurantsList);
        return false;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        String refId = marker.getTag().toString();
        for (Restaurant restaurant : restaurantsList) {
            if(restaurant.getRid().equals(refId)) {
                RestaurantWithDistance restaurantWithDistance = new RestaurantWithDistance(restaurant.getRid(),
                        restaurant.getName(), restaurant.getPhotos(), restaurant.getAddress(),
                        restaurant.getRating(), restaurant.getOpeningHours(),
                        restaurant.getPhoneNumber(), restaurant.getWebsite(), restaurant.getGeometry(),
                        DataProcessingUtils.calculateRestaurantDistance(restaurant, home));

                launchDetailRestaurantActivity(restaurantWithDistance);
                break;
            }
        }
        return true;
    }

    @SuppressWarnings("MissingPermission")  // Permissions already checked in MainActivity
    private void initializeMap() {
        // Check permissions
        locationPermissionsGranted = MapsApisUtils.arePermissionsGranted();
        if (mGoogleMap != null) {
            // Display MyLocation button if permissions are granted
            mGoogleMap.setMyLocationEnabled(locationPermissionsGranted);
            // Set camera default zoom
            mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
            // Other settings
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            // Set listener to my location button in order to know if home focus is requested
            mGoogleMap.setOnMyLocationButtonClickListener(this);
        }
        // Request for home focus at first display
        focusHome = true;
    }

    @SuppressLint("MissingPermission")  // Permissions already checked in MainActivity
    private void displayMap() {
        if (mGoogleMap != null) {
            /* If necessary, display again MyLocation button if permissions are granted
               (sometimes it is and I don't know why) */
            locationPermissionsGranted = MapsApisUtils.arePermissionsGranted();
            mGoogleMap.setMyLocationEnabled(locationPermissionsGranted);
            // Get data from API and display map with or without home focus
            //getDataFromApiAndFocusHome(); // TODO : To be deleted
            // Display restaurants
            restaurantsList = FirestoreUtils.getRestaurantsList();
            displayRestaurantsOnMap(restaurantsList);
            // Set Focus to home
            setFocusToHome();
        }
    }

    private void setFocusToHome() {
        home = MapsApisUtils.getHome();
        // Set camera position to home if requested
        if (focusHome) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, DEFAULT_ZOOM));
            // No more home focus after first display (except if MyLocationButton is triggered)
            focusHome = false;
        }
    }

    /*  // TODO : To be deleted
    @SuppressWarnings("MissingPermission")  // Permissions already checked in MainActivity
    public void getDataFromApiAndFocusHome() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            // Task<Location> locationResult = fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null);
            locationResult.addOnCompleteListener(requireActivity(), task -> {
                if (task.isSuccessful()) {
                    Location lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null) {
                        // Get the current location...
                        double latitude = lastKnownLocation.getLatitude();
                        double longitude = lastKnownLocation.getLongitude();
                        // Initialize home
                        home = new LatLng(latitude, longitude);
                        // Initialize home in MapsApisUtils to make it available for ListViewFragment
                        MapsApisUtils.setHome(home);
                        // Set camera position to home if requested
                        if (focusHome) {
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, DEFAULT_ZOOM));
                            // No more home focus after first display (except if MyLocationButton is triggered)
                            focusHome = false;
                        }
                        // Get nearby restaurants list from API
                        restaurantsList = MapsApisUtils.getRestaurantsFromApi(requireActivity(), home, getString(R.string.MAPS_API_KEY));
                        // Reinitialize restaurantList in FirestoreUtils to make it available for ListViewFragment
                        restaurantsList = FirestoreUtils.getRestaurantsListFromDatabaseDocument();
                        // Display restaurants on map
                        displayRestaurantsOnMap(restaurantsList);
                    } else {
                        getUpdatedDataFromApiAndFocusHome();
                    }
                }
                else {
                    Log.w("getDeviceLocation", "Exception: %s", task.getException());
                }
            });

        } catch (SecurityException e) {
            Log.w("Exception: %s", e.getMessage(), e);
        }
    }

    @SuppressWarnings("MissingPermission")  // Permissions already checked in MainActivity
    private void getUpdatedDataFromApiAndFocusHome() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        // Setup parameters of location request
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500).build();
        // Create callback to handle location result
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        // Get the current location...
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        // ...and stop location updates as soon of current location is got
                        fusedLocationProviderClient.removeLocationUpdates(this);
                        Snackbar.make(binding.getRoot(), getString(R.string.info_location_updated), Snackbar.LENGTH_LONG).show();
                        // Initialize home
                        home = new LatLng(latitude, longitude);
                        // Initialize home in MapsApisUtils to make it available for ListViewFragment
                        MapsApisUtils.setHome(home);
                        // Set camera position to home if requested
                        if (focusHome) {
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, DEFAULT_ZOOM));
                            // No more home focus after first display (except if MyLocationButton is triggered)
                            focusHome = false;
                        }
                        // Get nearby restaurants list from API
                        restaurantsList = MapsApisUtils.getRestaurantsFromApi(requireActivity(), home, getString(R.string.MAPS_API_KEY));
                        // Reinitialize restaurantList in FirestoreUtils to make it available for ListViewFragment
                        restaurantsList = FirestoreUtils.getRestaurantsListFromDatabaseDocument();
                        // Display restaurants on map
                        displayRestaurantsOnMap(restaurantsList);
                    }
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    */

    @SuppressLint("PotentialBehaviorOverride")  // This remark concerns OnMarkerClickListener below
    private void displayRestaurantsOnMap(List<Restaurant> restaurants) {
        if (restaurants != null) {
            for (Restaurant restaurant : restaurants) {
                double rLat = restaurant.getGeometry().getLocation().getLat();
                double rLng = restaurant.getGeometry().getLocation().getLng();
                String rId = restaurant.getRid();

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(rLat, rLng))
                        .title(restaurant.getName()));
                marker.setTag(rId);
                getSelectionsCountAndUpdateMarkerIcon(rId, marker);
            }
            mGoogleMap.setOnMarkerClickListener(this);
        }
    }

    private void launchDetailRestaurantActivity(RestaurantWithDistance restaurant) {
        Intent intent = new Intent(requireActivity(), DetailRestaurantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("RESTAURANT", restaurant);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void getSelectionsCountAndUpdateMarkerIcon(String rId, Marker marker) {
        workmatesList = FirestoreUtils.getWorkmatesList();
        // workmatesList = FirestoreUtils.getWorkmatesListFromDatabaseDocument();   // TODO : May not keep coherence between fragments
        selectionsCount = 0;
        for (User workmate : workmatesList) {
            // For each workmate, check selected restaurant and increase selections count if matches with restaurant id
            boolean isSelected = rId.equals(workmate.getSelectionId()) && currentDate.equals(workmate.getSelectionDate());
            if (isSelected) selectionsCount += 1;
        }
        // Update marker color
        float markerColor = (selectionsCount > 0) ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED;
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(markerColor));
    }

    // Autocomplete launched from activity
    public void launchAutocomplete(String text) {
        autocompleteCardView.setVisibility(View.VISIBLE);
        configureAutocompleteSupportFragment(text);
    }

    private void configureAutocompleteSupportFragment(String text) {
        // Specify the limitation to only show results within the defined region
        LatLngBounds latLngBounds = DataProcessingUtils.calculateBounds(home, Integer.parseInt(MapsApisUtils.getSearchRadius())*1000);
        autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(latLngBounds.southwest, latLngBounds.northeast));
        autocompleteFragment.setActivityMode(AutocompleteActivityMode.valueOf("FULLSCREEN"));
        autocompleteFragment.setText(text);

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                String placeId = place.getId();
                Log.i("MapViewFragment", "Place: " + placeId);
                for (Restaurant restaurant : restaurantsList) {
                    if (Objects.equals(restaurant.getRid(), placeId)) {
                        double lat = restaurant.getGeometry().getLocation().getLat();
                        double lng = restaurant.getGeometry().getLocation().getLng();
                        Log.i("MapViewFragment", "Place: " + placeId + ", " + lat + ", " + lng);
                        // Focus map on this restaurant
                        if (mGoogleMap != null) mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), RESTAURANT_ZOOM));
                        autocompleteFragment.setText("");
                        autocompleteCardView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("MainActivity", "An error occurred: " + status);
                autocompleteFragment.setText("");
                autocompleteCardView.setVisibility(View.GONE);
            }
        });
    }


}