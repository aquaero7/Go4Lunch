package com.example.go4lunch.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.go4lunch.fragment.SettingsFragment;
import com.example.go4lunch.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        configureAndShowSettingsFragment();
    }

    private void configureAndShowSettingsFragment() {
        // Get FragmentManager (Support) and try to find existing instance of fragment in FrameLayout container
        SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.frameLayoutSettings);
        if (settingsFragment == null) {
            // Create new settings fragment
            settingsFragment = new SettingsFragment();
            // Add it to FrameLayout container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayoutSettings, settingsFragment)
                    .commit();
        }
    }

}