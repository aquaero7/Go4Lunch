package com.example.go4lunch.view.fragment;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.example.go4lunch.utils.EventObjectClick;
import com.example.go4lunch.view.activity.DetailRestaurantActivity;
import com.example.go4lunch.databinding.FragmentMapViewBinding;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.utils.EventListener;
import com.example.go4lunch.viewmodel.MapViewViewModel;
import com.example.go4lunch.viewmodel.ViewModelFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class MapViewFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener,
        OnMapsSdkInitializedCallback {

    private FragmentMapViewBinding binding;
    private EventListener eventListener;
    private MapViewViewModel mapViewViewModel;
    private GoogleMap mGoogleMap;

    public MapViewFragment() {
    }

    // Factory method to create a new instance of this fragment
    public static MapViewFragment newInstance() {
        return (new MapViewFragment());
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Set the layout file as the content view.
        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        // Initialize ViewModel
        mapViewViewModel = new ViewModelProvider(requireActivity(), new ViewModelFactory()).get(MapViewViewModel.class);
        // Show ProgressBar
        binding.progressbarMap.progressbar.setVisibility(View.VISIBLE);
        // Register autocomplete activity result
        createAutocompleteResultLauncher();

        /** To use if menu is handled in fragment
         * Works with onCreateOptionsMenu() and onOptionsItemSelected() */
        setHasOptionsMenu(true);

        // Require latest version of map renderer   // Not working : Legacy version is used anyway !
        MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST, this);
        // Initialize SDK Places for Autocomplete API
        Places.initialize(requireContext(), getString(R.string.MAPS_API_KEY));

        // return rootView;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // Acquiring the map
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Setup toolbar title (Activity title)
        requireActivity().setTitle(R.string.mapView_toolbar_title);
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
        initMap();
        setFocusToHome();
        displayRestaurantsOnMap();
        // Show message if location permissions are denied
        if (!mapViewViewModel.arePermissionsGranted()) Snackbar
                .make(binding.getRoot(), getString(R.string.info_no_permission), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Request for home focus and default zoom
        mapViewViewModel.setFocusHome(true);
        // Reset the zoom
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(mapViewViewModel.getDefaultZoom()));
        return false;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        for (RestaurantWithDistance restaurant : mapViewViewModel.getRestaurants()) {
            if(restaurant.getRid().equals(marker.getTag())) {
                launchDetailRestaurantActivity(restaurant);
                break;
            }
        }
        return true;
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
        switch (EventObjectClick.fromMenuItem(item)) {
            case MENU_ITEM_SEARCH:
                eventListener.toggleSearchViewVisibility();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("MissingPermission")  // Permissions already checked in MainActivity
    private void initMap() {
        // Display MyLocation button if permissions are granted
        mGoogleMap.setMyLocationEnabled(mapViewViewModel.arePermissionsGranted());
        // Set camera default zoom
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(mapViewViewModel.getDefaultZoom()));
        // Other settings
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        // Set listener to my location button in order to know if home focus is requested
        mGoogleMap.setOnMyLocationButtonClickListener(this);
        // Request for home focus at first display
        mapViewViewModel.setFocusHome(true);
    }

    @SuppressLint("MissingPermission")  // Permissions already checked in AuthActivity
    private void setFocusToHome() {
        // Initialize or update location data
        mapViewViewModel.getCurrentLocationMutableLiveData().observe(requireActivity(), home -> {
            // Set camera position to home if requested
            if (home != null && mapViewViewModel.getFocusHome()) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, mapViewViewModel.getDefaultZoom()));
                // No more home focus after first display (except if MyLocationButton is triggered)
                mapViewViewModel.setFocusHome(false);
            }
        });

    }

    @SuppressLint({"MissingPermission", "PotentialBehaviorOverride"})
    // MissingPermission: Permissions already checked in AuthActivity
    // PotentialBehaviorOverride: This remark concerns OnMarkerClickListener below
    private void displayRestaurantsOnMap() {
        // Initialize restaurants data
        mapViewViewModel.getRestaurantsMutableLiveData().observe(requireActivity(), restaurants -> {
            // Display restaurants on map
            if (!restaurants.isEmpty()) {
                for (RestaurantWithDistance restaurant : restaurants) {
                    // Add marker
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(mapViewViewModel.getLatLng(restaurant))
                            .title(restaurant.getName()));
                    Objects.requireNonNull(marker).setTag(restaurant.getRid());
                    getSelectionsCountAndUpdateMarkerIcon(restaurant.getRid(), marker);
                }
                // Add listener on restaurants markers
                mGoogleMap.setOnMarkerClickListener(this);
                // Hide ProgressBar
                binding.progressbarMap.progressbar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void getSelectionsCountAndUpdateMarkerIcon(String rId, Marker marker) {
        // Initialize or update restaurants data
        mapViewViewModel.getWorkmatesMutableLiveData().observe(requireActivity(), workmatesList -> {
            // Update marker color
            float markerColor = (mapViewViewModel.getSelectionsCount(rId, workmatesList) > 0) ?
                    BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED;
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(markerColor));
        });
    }

    private void launchDetailRestaurantActivity(RestaurantWithDistance restaurant) {
        Intent intent = new Intent(requireActivity(), DetailRestaurantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("RESTAURANT", restaurant);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void createAutocompleteResultLauncher() {
        // Manage predictions user selection
        ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            Place place = Autocomplete.getPlaceFromIntent(intent);
                            Log.w("MapViewFragment", "Place: ${place.getId()}");
                            for (RestaurantWithDistance restaurant : mapViewViewModel.getRestaurants()) {
                                if (Objects.equals(restaurant.getRid(), place.getId())) {
                                    // Focus map on this restaurant
                                    if (mGoogleMap != null) mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            mapViewViewModel.getLatLng(restaurant),
                                            mapViewViewModel.getRestaurantZoom()));
                                    Log.w("MapViewFragment", "Place: " + place.getId()
                                            + " Lat : " + mapViewViewModel.getLatLng(restaurant).latitude
                                            + " Lng : " + mapViewViewModel.getLatLng(restaurant).longitude);
                                }
                            }
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Log.w("MapViewFragment", "User canceled autocomplete");
                    }
                });
        mapViewViewModel.setStartAutocomplete(startAutocomplete);
    }

    public void launchAutocomplete(String query) {
        mapViewViewModel.launchAutocomplete(query, requireContext());
    }

}