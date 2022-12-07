package com.example.go4lunch.Fragments;


import static android.content.Context.LOCATION_SERVICE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapViewFragment extends Fragment {

    private SupportMapFragment mapFragment;

    private Context mContext;
    private LocationManager mLocationManager;
    private GoogleMap mGoogleMap;
    private LatLng home;
    private double latitude;
    private double longitude;
    private static final int PERMS_CALL_ID = 1234;
    private static final int DEFAULT_ZOOM = 15;


    // Factory method to create a new instance of this fragment
    public static MapViewFragment newInstance() {
        return (new MapViewFragment());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Set the layout file as the content view.
        return inflater.inflate(R.layout.fragment_map_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get a handle to the fragment and register the callback.
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mContext = requireContext();
        checkPermissionsAndLoadMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Setup toolbar title (Activity title)
        requireActivity().setTitle(R.string.mapView_toolbar_title);
        checkPermissionsAndLoadMap();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMS_CALL_ID) {
            checkPermissionsAndLoadMap();
        }
    }


    // Listen to locations changes and get current position
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Toast.makeText(mContext, "Location : " + latitude + " / " + longitude, Toast.LENGTH_LONG).show();   // TODO : Test to be deleted
            if (mGoogleMap != null) {
                home = new LatLng(latitude, longitude);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(home));
            }
        }
    };

    private void checkPermissionsAndLoadMap() {

        // Check and require missing permissions
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO : See the documentation for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(requireActivity(), new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMS_CALL_ID);

            return;
        }

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

        loadMap();
    }

    @SuppressWarnings("MissingPermission")
    // Permissions already checked in checkPermissionsAndLoadMap() method, called in onResume() method
    private void loadMap() {
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                /* Manipulates the map once available.
                This callback is triggered when the map is ready to be used.
                This is where we can add markers or lines, add listeners or move the camera. */

                // Set position on map
                mGoogleMap = googleMap;
                home = new LatLng(latitude, longitude);
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, DEFAULT_ZOOM));
                // mGoogleMap.moveCamera(CameraUpdateFactory.zoomBy(20));

                // Customize map display
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(48.7270, 2.1200))
                        .title("Marker in VLB"));
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(43.0900, 5.8400))
                        .title("Marker in SFLP"));

            });
        }
    }

}