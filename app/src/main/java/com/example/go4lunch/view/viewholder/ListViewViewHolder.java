package com.example.go4lunch.view.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.RestaurantListItemBinding;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ListViewViewHolder extends RecyclerView.ViewHolder {

    private final TextView tvTitle;
    private final TextView tvDistance;
    private final TextView tvSelectionsCount;
    private final TextView tvOpenTime;
    private final ImageView ivWorkmateLogo;
    private final RatingBar mRatingBar;
    private final ImageView ivPicture;

    private final String currentDate = DataProcessingUtils.getCurrentDate();


    public ListViewViewHolder(@NonNull View itemView) {
        super(itemView);
        RestaurantListItemBinding binding = RestaurantListItemBinding.bind(itemView);

        tvTitle = binding.restaurantItemTitle;
        tvDistance = binding.restaurantItemDistance;
        ivWorkmateLogo = binding.restaurantItemWorkmateLogo;
        tvSelectionsCount = binding.restaurantItemSelectionsCount;
        tvOpenTime = binding.restaurantItemOpenTime;
        mRatingBar = binding.restaurantItemRatingBar;
        ivPicture = binding.restaurantItemPicture;
    }


    public void updateWithRestaurants(RestaurantWithDistance restaurant, List<User> workmatesList,
                                      String KEY, String STATUS_OPEN, String STATUS_CLOSED, String STATUS_UNKNOWN) {

        // Display restaurant name and Setup text scrolling
        tvTitle.setText(restaurant.getName());
        tvTitle.setSelected(true);
        // Display restaurant distance
        displayRestaurantDistance(restaurant);
        // Display workmate logo
        ivWorkmateLogo.setImageResource(R.drawable.ic_outline_person_black_24);
        // Display selections count
        displaySelectionsCount(restaurant.getRid(), workmatesList);
        // Display restaurant opening info
        displayRestaurantOpeningInfo(restaurant, STATUS_OPEN, STATUS_CLOSED, STATUS_UNKNOWN);
        // Display restaurant rating
        mRatingBar.setRating((float) (restaurant.getRating() * 3/5));
        // Display restaurant picture
        if (restaurant.getPhotos() != null) Picasso.get().load(restaurant.getPhotos().get(0).getPhotoUrl(KEY)).into(ivPicture);
    }


    private void displayRestaurantDistance(RestaurantWithDistance restaurant) {
        String distance;
        int dist = (int) restaurant.getDistance();
        if (dist < 10000) {
            distance = dist + "m";
        } else {
            distance = dist / 1000 + "km";
        }
        tvDistance.setText(distance);
    }

    private void displaySelectionsCount(String rId, List<User> workmates) {
        int selectionsCount = 0;
        for (User workmate : workmates) {
            // Check selected restaurant id and date and increase selections count if matches with restaurant id
            boolean isSelected = rId.equals(workmate.getSelectionId())
                    && currentDate.equals(workmate.getSelectionDate());
            if (isSelected) selectionsCount += 1;
        }
        // Display selections count
        if (selectionsCount > 0) {
            ivWorkmateLogo.setVisibility(View.VISIBLE);
            tvSelectionsCount.setVisibility(View.VISIBLE);
        } else {
            ivWorkmateLogo.setVisibility(View.INVISIBLE);
            tvSelectionsCount.setVisibility(View.INVISIBLE);
        }
        String workmatesCount = "(" + selectionsCount + ")";
        tvSelectionsCount.setText(workmatesCount);
    }

    private void displayRestaurantOpeningInfo(RestaurantWithDistance restaurant,
                                              String STATUS_OPEN, String STATUS_CLOSED, String STATUS_UNKNOWN) {
        // boolean openNow = Objects.requireNonNull(restaurant.getOpeningHours()).isOpenNow();
        // tvOpenTime.setText((openNow) ? STATUS_OPEN : STATUS_CLOSED);

        if (restaurant.getOpeningHours() != null) {
            tvOpenTime.setText((restaurant.getOpeningHours().isOpenNow()) ? STATUS_OPEN : STATUS_CLOSED);
        } else {
            tvOpenTime.setText(STATUS_UNKNOWN);
        }
    }

}
