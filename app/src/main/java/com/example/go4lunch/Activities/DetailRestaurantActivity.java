package com.example.go4lunch.Activities;

import static java.lang.String.valueOf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.go4lunch.Fragments.DetailRestaurantFragment;
import com.example.go4lunch.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class DetailRestaurantActivity extends AppCompatActivity implements DetailRestaurantFragment.OnButtonClickedListener {

    private Boolean fabChecked = false; // TODO : Update status with information in database


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

    }

    // --------------
    // CallBack
    // --------------
    @Override
    public void onButtonClicked(View view) {
        // Handle the FloatingActionButton click event

        FloatingActionButton selectionFab = findViewById(R.id.selection_fab);
        // Toggle FAB status
        fabChecked = !fabChecked;

        // Launch actions
        toggleSelectionFabDisplay(selectionFab);
        updateDatabase();
        displayToast();
    }

    private void toggleSelectionFabDisplay(FloatingActionButton fab) {
        // Define the foreground of the mipmap, according to new FAB status
        int resId = (fabChecked) ? R.mipmap.im_check_green_white : R.mipmap.im_check_grey_white;
        // Set up the new foreground
        fab.setForeground(AppCompatResources.getDrawable(this,resId));
    }

    private void updateDatabase() {
        // TODO : Update status in database
    }

    private void displayToast() {
        // Define the text of the toast, according to new FAB status
        String toastText = (fabChecked) ? getString(R.string.fabChecked) : getString(R.string.fabUnchecked);
        // Display the toast
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
    }


}