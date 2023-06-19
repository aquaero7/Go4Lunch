package com.example.go4lunch.view;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.databinding.WorkmateListItemBinding;
import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.CalendarUtils;
import com.squareup.picasso.Picasso;

public class WorkmateViewHolder extends RecyclerView.ViewHolder {

    private final ImageView imageView;
    private final TextView textView;
    private String mText;
    private final String currentDate = CalendarUtils.getCurrentDate();

    public WorkmateViewHolder(@NonNull View itemView) {
        super(itemView);
        WorkmateListItemBinding binding = WorkmateListItemBinding.bind(itemView);
        imageView = binding.workmateItemPicture;
        textView = binding.workmateItemTitle;
    }

    public void updateWithWorkmate(User workmate, String CHOICE_TEXT, String NO_CHOICE_TEXT) {
        // Display workmate picture
        displayWorkmatePicture(workmate);
        // Display workmate name
        displayWorkmateName(workmate, CHOICE_TEXT, NO_CHOICE_TEXT);
    }


    private void displayWorkmatePicture(User workmate) {
        String pictureUrl = workmate.getUserUrlPicture();
        if (pictureUrl != null && !pictureUrl.equals("")) {
            Picasso.get().load(pictureUrl).into(imageView);
            imageView.setColorFilter(Color.argb(0, 0, 0, 0));
        }
    }

    private void displayWorkmateName(User workmate, String CHOICE_TEXT, String NO_CHOICE_TEXT) {
        boolean isSelected = workmate.getSelectionId() != null && currentDate.equals(workmate.getSelectionDate());
        if (isSelected) {
            // Get selected restaurant name from database
            RestaurantManager.getInstance().getRestaurantData(workmate.getSelectionId())
                    .addOnSuccessListener(restaurant -> {
                        mText = workmate.getUsername() + CHOICE_TEXT + "\"" + restaurant.getName() + "\"";
                        textView.setText(mText);
                    })
                    .addOnFailureListener(e -> Log.w("WorkmateViewHolder", e.getMessage()));
        } else {
            // mText = workmate.getUsername() + NO_CHOICE_TEXT;
            mText = workmate.getUsername();
            textView.setText(mText);
        }
    }

}
