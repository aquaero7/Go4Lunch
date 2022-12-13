package com.example.go4lunch.Fragments;


import static android.content.Context.LOCATION_SERVICE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentMapViewBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;


public class MapViewFragment extends Fragment {

    private FragmentMapViewBinding binding;
    private SupportMapFragment mapFragment;
    private Activity mActivity;
    private Context mContext;
    private LocationManager mLocationManager;
    private GoogleMap mGoogleMap;
    private LatLng home;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private double latitude;
    private double longitude;
    private boolean locationPermissionsGranted = false;
    private static final double defaultLatitude = 48.8566;
    private static final double defaultLongitude = 2.3522;
    private static final int DEFAULT_ZOOM = 15;
    private final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private ActivityResultLauncher<String> requestPermissionsLauncher;

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

        // Initialize the activity and the context
        mActivity = requireActivity();
        mContext = requireContext();

        // Require latest version of map renderer
        getLatestRenderer();

        // Initialize SDK Places
        Places.initialize(mContext, getString(R.string.MAPS_API_KEY));
        // Create a new PlacesClient instance
        placesClient = Places.createClient(mContext);
        // Create a new FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity);

        // Register permissions callback
        registerPermissionsCallback();

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
        /** SOLUTION 1. Doesn't focus on current location at first display */   // getDeviceLocation();
        /** SOLUTION 2. Focus ok on current location at first display */        getUpdatedPosition();
        loadMap();
    }

    @Override
    public void onPause() {
        super.onPause();

        /** USED WITH SOLUTION 2 */      disconnectListenerFromUpdates();
    }


    private void getLatestRenderer() {
        MapsInitializer.initialize(mContext, MapsInitializer.Renderer.LATEST, renderer -> {
            switch (renderer) {
                case LATEST:
                    Log.d("MapsDemo", "The latest version of the renderer is used.");
                    break;
                case LEGACY:
                    Log.d("MapsDemo", "The legacy version of the renderer is used.");
                    break;
            }
        });
    }

    private void registerPermissionsCallback() {
        ActivityResultContracts.RequestPermission permissionsContract = new ActivityResultContracts.RequestPermission();
        requestPermissionsLauncher = registerForActivityResult(permissionsContract, isGranted -> {
            if (isGranted) {
                locationPermissionsGranted = true;
                Log.w("ActivityResultLauncher", "At least one permission was granted");
            } else {
                // locationPermissionsGranted = false;
                Log.w("ActivityResultLauncher", "No permission was granted");
            }
        });
    }

    private void checkPermissions() {
        // Check and request permissions
        if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            && (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Log.w("checkPermissions", "Permissions not granted");
            // The registered ActivityResultCallback gets the result of this(these) request(s).
            for (String permission : PERMISSIONS) {
                requestPermissionsLauncher.launch(permission);
            }
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
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(mActivity, task -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            latitude = lastKnownLocation.getLatitude();
                            longitude = lastKnownLocation.getLongitude();
                        } else {
                            latitude = defaultLatitude;
                            longitude = defaultLongitude;
                            Toast.makeText(mActivity, R.string.info_no_current_location, Toast.LENGTH_SHORT).show();
                            Log.w("getDeviceLocation", "Exception: %s", task.getException());
                        }
                    }
                });
            } else {
                latitude = defaultLatitude;
                longitude = defaultLongitude;
                Toast.makeText(mActivity, R.string.info_no_permission, Toast.LENGTH_SHORT).show();
                Log.w("getDeviceLocation", "Permissions not granted");
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
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

    // USED WITH SOLUTION 2 ////////////////////////////////////////////////////////////////////////

    // Listen to locations changes and get current position
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            // Update location metrics
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    };

    @SuppressWarnings("MissingPermission")
    // Permissions already checked in checkPermissionsAndLoadMap() method, called in onResume() method
    private void getUpdatedPosition() {
        if (locationPermissionsGranted) {
            // Get updated position from system and send it to listeners
            mLocationManager = (LocationManager) requireActivity().getSystemService(LOCATION_SERVICE);
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, mLocationListener);
            }
            if (mLocationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 0, mLocationListener);
            }
            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, mLocationListener);
            }
        } else {
            latitude = defaultLatitude;
            longitude = defaultLongitude;
            Toast.makeText(mActivity, R.string.info_no_permission, Toast.LENGTH_SHORT).show();
            Log.w("getDeviceLocation", "Permissions not granted");
        }
    }

    private void disconnectListenerFromUpdates() {
        // Disconnect listener from system updates
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

}