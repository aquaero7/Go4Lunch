package com.example.go4lunch.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PermissionsUtils extends AppCompatActivity {

    //
    private final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private ActivityResultLauncher<String[]> requestPermissionsLauncher;
    private static boolean locationPermissionsGranted = false;

    public static boolean arePermissionsGranted() {
        return locationPermissionsGranted;
    }

    public void registerPermissionsCallback() {
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
                requestPermissionsLauncher.launch(PERMISSIONS);
                Log.w("ActivityResultLauncher", "No location permission was granted");
            }
        });
    }

    private void checkPermissions(Context context) {
        // Check and request permissions
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Log.w("checkPermissions", "Permissions not granted");
            // The registered ActivityResultCallback gets the result of this(these) request(s).
            requestPermissionsLauncher.launch(PERMISSIONS);
        } else {
            Log.w("checkPermissions", "Permissions granted");
            locationPermissionsGranted = true;
        }
    }




    //

}
