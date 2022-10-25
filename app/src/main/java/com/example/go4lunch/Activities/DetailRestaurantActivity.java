package com.example.go4lunch.Activities;

import static java.lang.String.valueOf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.go4lunch.Fragments.DetailRestaurantFragment;
import com.example.go4lunch.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class DetailRestaurantActivity extends AppCompatActivity implements DetailRestaurantFragment.OnButtonClickedListener {

    private Boolean fabChecked = false; // TODO : Update status with information in database
    private String toastText;


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
        String tag = String.valueOf(view.getTag());
        switch (tag) {
            case "BTN_CALL" :
                // Launch action    TODO
                toastText = tag;    // TODO : Delete after action completion
                break;
            case "BTN_LIKE" :
                // Launch action    TODO
                toastText = tag;    // TODO : Delete after action completion
                break;
            case "BTN_WEBSITE" :
                // Launch action    TODO
                toastText = tag;    // TODO : Delete after action completion
                break;
            case "FAB" :
                FloatingActionButton selectionFab = findViewById(R.id.selection_fab);
                // Toggle FAB status
                fabChecked = !fabChecked;
                // Update toast text
                toastText = (fabChecked) ? getString(R.string.fabChecked) : getString(R.string.fabUnchecked);
                // Launch actions
                toggleSelectionFabDisplay(selectionFab);
                updateDatabase();
                break;
        }

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
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
    }




}