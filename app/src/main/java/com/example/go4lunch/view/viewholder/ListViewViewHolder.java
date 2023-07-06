package com.example.go4lunch.view.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.RestaurantListItemBinding;
import com.squareup.picasso.Picasso;

public class ListViewViewHolder extends RecyclerView.ViewHolder {

    RestaurantListItemBinding binding;

    public ListViewViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = RestaurantListItemBinding.bind(itemView);
    }

    public void updateWithRestaurants(String name, double rating, String distance, int selectionsCount,
                                      String openingInfo, String photoUrl) {

        // Display restaurant name and Setup text scrolling
        binding.restaurantItemTitle.setText(name);
        binding.restaurantItemTitle.setSelected(true);
        // Display restaurant distance
        binding.restaurantItemDistance.setText(distance);
        // Display workmate logo
        binding.restaurantItemWorkmateLogo.setImageResource(R.drawable.ic_outline_person_black_24);
        // Display selections count
        displaySelectionsCount(selectionsCount);
        // Display restaurant opening info
        binding.restaurantItemOpenTime.setText(openingInfo);
        // Display restaurant rating
        binding.restaurantItemRatingBar.setRating((float) (rating * 3/5));
        // Display restaurant picture
        if (photoUrl != null && !photoUrl.equals("")) Picasso.get().load(photoUrl).into(binding.restaurantItemPicture);
    }

    private void displaySelectionsCount(int selectionsCount) {
        if (selectionsCount > 0) {
            binding.restaurantItemWorkmateLogo.setVisibility(View.VISIBLE);
            binding.restaurantItemSelectionsCount.setVisibility(View.VISIBLE);
        } else {
            binding.restaurantItemWorkmateLogo.setVisibility(View.INVISIBLE);
            binding.restaurantItemSelectionsCount.setVisibility(View.INVISIBLE);
        }
        String workmatesCount = "(" + selectionsCount + ")";
        binding.restaurantItemSelectionsCount.setText(workmatesCount);
    }

}
