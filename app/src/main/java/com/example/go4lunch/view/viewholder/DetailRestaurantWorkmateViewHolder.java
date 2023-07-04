package com.example.go4lunch.view.viewholder;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.databinding.WorkmateListItemBinding;
import com.squareup.picasso.Picasso;

public class DetailRestaurantWorkmateViewHolder extends RecyclerView.ViewHolder {

    WorkmateListItemBinding binding;

    public DetailRestaurantWorkmateViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = WorkmateListItemBinding.bind(itemView);
    }

    public void updateWithSelector(String pictureUrl, String nameAndText) {
        // Display selector picture
        if (pictureUrl != null && !pictureUrl.equals("")) {
            Picasso.get().load(pictureUrl).into(binding.workmateItemPicture);
            binding.workmateItemPicture.setColorFilter(Color.argb(0, 0, 0, 0));
        }
        // Display workmate name
        binding.workmateItemTitle.setText(nameAndText);
    }

}
