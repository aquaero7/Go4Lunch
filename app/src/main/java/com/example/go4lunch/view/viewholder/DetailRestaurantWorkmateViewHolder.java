package com.example.go4lunch.view.viewholder;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.databinding.WorkmateListItemBinding;
import com.example.go4lunch.model.model.User;
import com.squareup.picasso.Picasso;

public class DetailRestaurantWorkmateViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView textView;

    public DetailRestaurantWorkmateViewHolder(@NonNull View itemView) {
        super(itemView);
        WorkmateListItemBinding binding = WorkmateListItemBinding.bind(itemView);
        imageView = binding.workmateItemPicture;
        textView = binding.workmateItemTitle;
    }

    public void updateWithSelector(User selector, String JOINING_TEXT) {
        // Display selector picture
        displaySelectorPicture(selector);
        // Display workmate name
        displayWorkmateName(selector, JOINING_TEXT);
    }


    private void displaySelectorPicture(User selector) {
        String pictureUrl = selector.getUserUrlPicture();
        if (pictureUrl != null && pictureUrl != "") {
            Picasso.get().load(pictureUrl).into(imageView);
            imageView.setColorFilter(Color.argb(0, 0, 0, 0));
        }
    }

    private void displayWorkmateName(User selector, String JOINING_TEXT) {
        String mText = selector.getUsername() + JOINING_TEXT;
        textView.setText(mText);
    }

}
