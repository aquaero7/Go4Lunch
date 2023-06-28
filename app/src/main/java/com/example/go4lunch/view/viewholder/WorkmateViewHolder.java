package com.example.go4lunch.view.viewholder;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.databinding.WorkmateListItemBinding;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class WorkmateViewHolder extends RecyclerView.ViewHolder {

    private final ImageView imageView;
    private final TextView textView;
    private String mText;
    private final String currentDate = DataProcessingUtils.getCurrentDate();

    public WorkmateViewHolder(@NonNull View itemView) {
        super(itemView);
        WorkmateListItemBinding binding = WorkmateListItemBinding.bind(itemView);
        imageView = binding.workmateItemPicture;
        textView = binding.workmateItemTitle;
    }

    public void updateWithWorkmate(User workmate, List<RestaurantWithDistance> restaurantsList, String CHOICE_TEXT, String NO_CHOICE_TEXT) {
        // Display workmate picture
        displayWorkmatePicture(workmate);
        // Display workmate name
        displayWorkmateName(workmate, restaurantsList, CHOICE_TEXT, NO_CHOICE_TEXT);
    }


    private void displayWorkmatePicture(User workmate) {
        String pictureUrl = workmate.getUserUrlPicture();
        if (pictureUrl != null && !pictureUrl.equals("")) {
            Picasso.get().load(pictureUrl).into(imageView);
            imageView.setColorFilter(Color.argb(0, 0, 0, 0));
        }
    }

    private void displayWorkmateName(User workmate, List<RestaurantWithDistance> restaurantsList, String CHOICE_TEXT, String NO_CHOICE_TEXT) {
        boolean isSelected = (workmate.getSelectionId() != null && currentDate.equals(workmate.getSelectionDate()));
        // Get user name
        mText = workmate.getUsername();
        if (isSelected) {
            // Get selected restaurant name
            for (RestaurantWithDistance restaurant : restaurantsList) {
                if (Objects.equals(workmate.getSelectionId(), restaurant.getRid())) {
                    mText = mText + CHOICE_TEXT + "\"" + restaurant.getName() + "\"";
                    break;
                }
            }
        }
        textView.setText(mText);
    }

}
