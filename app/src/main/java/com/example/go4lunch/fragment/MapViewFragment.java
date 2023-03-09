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
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.activity.DetailRestaurantActivity;
import com.example.go4lunch.databinding.FragmentMapViewBinding;
import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.CalendarUtils;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.example.go4lunch.utils.EventListener;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        autocompleteCardView = binding.includedAutocompleteCardView.autocompleteCardView;

        // Initialize AutocompleteSupportFragment
        // autocompleteFragment = (AutocompleteSupportFragment) getParentFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
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
                // Toast.makeText(requireContext(), "Click on search button in MapViewFragment", Toast.LENGTH_SHORT).show();   // TODO : To be deleted
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
        // Refresh restaurant list display
        if (restaurantsList.isEmpty()) restaurantsList = FirestoreUtils.getRestaurantsList();
        displayRestaurantsOnMap(restaurantsList);
        return false;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        String refId = marker.getTag().toString();
        for (Restaurant restaurant : restaurantsList) {
            if(restaurant.getId().equals(refId)) {
                launchDetailRestaurantActivity(restaurant);
                break;
            }
        }
        return false;
    }

    @SuppressWarnings("MissingPermission")  // Permissions already checked from MainActivity
    private void initializeMap() {
        // Check permissions
        locationPermissionsGranted = MapsApisUtils.arePermissionsGranted();
        // Get current location
        home = MapsApisUtils.getHome();
        // Display MyLocation button if permissions are granted
        mGoogleMap.setMyLocationEnabled(locationPermissionsGranted);
        // Set camera default zoom
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        // Other settings
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        // Request for home focus at first display
        focusHome = true;
        // Set listener to my location button in order to know if home focus is requested
        mGoogleMap.setOnMyLocationButtonClickListener(this);
    }

    @SuppressLint("MissingPermission")
    private void displayMap() {
        // Check permissions again to get current location and display MyLocation button if necessary
        locationPermissionsGranted = MapsApisUtils.arePermissionsGranted();
        // Get current location again
        home = MapsApisUtils.getHome();
        // Display MyLocation button again if necessary
        if (mGoogleMap != null) mGoogleMap.setMyLocationEnabled(locationPermissionsGranted);

        // Get restaurants list from Firestore
        if (restaurantsList.isEmpty()) restaurantsList = FirestoreUtils.getRestaurantsList();

        // Display map with or without home focus
        if (mGoogleMap != null && home != null) {
            // Set camera position if requested
            if (focusHome) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, DEFAULT_ZOOM));
            }
            // Display restaurants on map
            displayRestaurantsOnMap(restaurantsList);
        }
        // No more home focus after first display (except if MyLocationButton is triggered)
        focusHome = false;
    }

    @SuppressLint("PotentialBehaviorOverride")  // This remark concerns OnMarkerClickListener below
    private void displayRestaurantsOnMap(List<Restaurant> restaurants) {
        if (restaurants != null) {
            for (Restaurant restaurant : restaurants) {
                double rLat = restaurant.getGeometry().getLocation().getLat();
                double rLng = restaurant.getGeometry().getLocation().getLng();
                String rId = restaurant.getId();

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(rLat, rLng))
                        .title(restaurant.getName()));
                marker.setTag(rId);
                getSelectionsCountAndUpdateMarkerIcon(rId, marker);
            }
            mGoogleMap.setOnMarkerClickListener(this);
        }
    }

    private void launchDetailRestaurantActivity(Restaurant restaurant) {
        Intent intent = new Intent(requireActivity(), DetailRestaurantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("RESTAURANT", restaurant);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void getSelectionsCountAndUpdateMarkerIcon(String rId, Marker marker) {
        // Get workmates list
        UserManager.getUsersList(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    selectionsCount = 0;
                    // Get users list
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> userData = document.getData(); // TODO : Map data for debug. To be deleted
                        // Get workmate in workmates list
                        User workmate = FirestoreUtils.getUserFromDatabaseDocument(document);
                        // Check selected restaurant and increase selections count if matches with restaurant id
                        boolean isSelected = rId.equals(workmate.getSelectionId()) && currentDate.equals(workmate.getSelectionDate());
                        if (isSelected) selectionsCount += 1;
                    }
                    // Update marker color
                    float markerColor = (selectionsCount > 0) ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED;
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(markerColor));
                }
            } else {
                Log.w("MapViewFragment", "Error getting documents: ", task.getException());
                // Toast.makeText(requireContext(), "Error retrieving users list from database", Toast.LENGTH_SHORT).show();    // TODO : For debug
            }
        });
    }

    // Autocomplete launched from activity
    public void launchAutocomplete(String text) {
        autocompleteCardView.setVisibility(View.VISIBLE);
        configureAutocompleteSupportFragment(text);
    }

    private void configureAutocompleteSupportFragment(String text) {
        // Specify the limitation to only show results within the defined region
        LatLngBounds latLngBounds = DataProcessingUtils.calculateBounds(home, MapsApisUtils.getDefaultRadius());
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
                    if (Objects.equals(restaurant.getId(), placeId)) {
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