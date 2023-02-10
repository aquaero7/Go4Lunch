package com.example.go4lunch.view;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.WorkmateListItemBinding;
import com.example.go4lunch.manager.SelectedRestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.User;
import com.squareup.picasso.Picasso;

public class WorkmateViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView textView;
    String mText;

    public WorkmateViewHolder(@NonNull View itemView) {
        super(itemView);
        WorkmateListItemBinding binding = WorkmateListItemBinding.bind(itemView);
        imageView = binding.workmateItemPicture;
        textView = binding.workmateItemTitle;

        /*  // TODO : to be deleted cause replaced with ViewBinding
        imageView = itemView.findViewById(R.id.workmate_item_picture);
        textView = itemView.findViewById(R.id.workmate_item_title);
        */
    }

    public void updateWithWorkmate(User workmate, String CHOICE_TEXT, String NO_CHOICE_TEXT) {

        // Display workmate picture
        String pictureUrl = workmate.getUserUrlPicture();
        if (pictureUrl != null && pictureUrl != "") {
            Picasso.get().load(pictureUrl).into(imageView);
            imageView.setColorFilter(Color.argb(0, 0, 0, 0));
        }

        // Display workmate name
        if (workmate.getSelectedRestaurantId() != null && workmate.getSelectedRestaurantId() != "") {

            // Get workmate selected restaurant id from database
            UserManager.getInstance().getUserData(workmate.getUid()).addOnSuccessListener(user -> {
                // Get selected restaurant name from database
                SelectedRestaurantManager.getRestaurantData(user.getSelectedRestaurantId())
                        .addOnSuccessListener(restaurant -> {
                            // mText = workmate.getUsername() + " is eating at \"" + restaurant.getName() + "\"";
                            mText = workmate.getUsername() + CHOICE_TEXT + "\"" + restaurant.getName() + "\"";
                            textView.setText(mText);
                        })
                        .addOnFailureListener(e -> Log.w("WorkmateViewHolder", e.getMessage()));
            });
        } else {
            // mText = workmate.getUsername() + " is probably eating somewhere...";
            mText = workmate.getUsername() + NO_CHOICE_TEXT;
            textView.setText(mText);
        }

    }

}
