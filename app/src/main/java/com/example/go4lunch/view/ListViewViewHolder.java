package com.example.go4lunch.view;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.RestaurantListItemBinding;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.CalendarUtils;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.example.go4lunch.utils.FirestoreUtils;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ListViewViewHolder extends RecyclerView.ViewHolder {

    private final TextView tvTitle;
    private final TextView tvDistance;
    private final TextView tvAddress;
    private final TextView tvSelectionsCount;
    private final TextView tvOpenTime;
    private final ImageView ivWorkmateLogo;
    private final RatingBar mRatingBar;
    private final ImageView ivPicture;

    private final String currentDate = CalendarUtils.getCurrentDate();


    public ListViewViewHolder(@NonNull View itemView) {
        super(itemView);
        RestaurantListItemBinding binding = RestaurantListItemBinding.bind(itemView);

        tvTitle = binding.restaurantItemTitle;
        tvDistance = binding.restaurantItemDistance;
        tvAddress = binding.restaurantItemAddress;
        ivWorkmateLogo = binding.restaurantItemWorkmateLogo;
        tvSelectionsCount = binding.restaurantItemSelectionsCount;
        tvOpenTime = binding.restaurantItemOpenTime;
        mRatingBar = binding.restaurantItemRatingBar;
        ivPicture = binding.restaurantItemPicture;
    }


    public void updateWithRestaurants(RestaurantWithDistance restaurant, List<User> workmatesList, String KEY, String STATUS_OPEN,
                                      String STATUS_CLOSED, String STATUS_OPEN247,
                                      String STATUS_OPEN24, String STATUS_OPEN_UNTIL,
                                      String STATUS_OPEN_AT, String STATUS_UNKNOWN) {

        // Display restaurant name
        tvTitle.setText(restaurant.getName());
        // Display restaurant distance
        displayRestaurantDistance(restaurant);
        // Display restaurant address
        tvAddress.setText(restaurant.getAddress());
        // Display workmate logo
        ivWorkmateLogo.setImageResource(R.drawable.ic_outline_person_black_24);
        // Display selections count
        displaySelectionsCount(restaurant.getRid(), workmatesList);
        // Display restaurant opening info
        displayRestaurantOpeningInfo(restaurant, KEY, STATUS_OPEN, STATUS_CLOSED, STATUS_OPEN247,
                STATUS_OPEN24, STATUS_OPEN_UNTIL, STATUS_OPEN_AT, STATUS_UNKNOWN);
        // Display restaurant rating
        mRatingBar.setRating((float) (restaurant.getRating() * 3/5));
        // Display restaurant picture
        if (restaurant.getPhotos() != null) Picasso.get().load(restaurant.getPhotos().get(0).getPhotoUrl(KEY)).into(ivPicture);
        // Setup text scrolling
        setupTextScrolling();
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

    private void displayRestaurantOpeningInfo(RestaurantWithDistance restaurant, String KEY, String STATUS_OPEN,
                                              String STATUS_CLOSED, String STATUS_OPEN247,
                                              String STATUS_OPEN24, String STATUS_OPEN_UNTIL,
                                              String STATUS_OPEN_AT, String STATUS_UNKNOWN) {
        String information = DataProcessingUtils.getOpeningInformation(restaurant);
        if (!information.isEmpty()) {
            int lim = 7;    // Information must be 7 char length max
            int sep = 3;    // Position of separator between code and schedule
            String infoStart = information.substring(0,sep);
            String infoEnd = information.substring(sep);
            String infoToDisplay = "";
            if (information.length() == lim) infoToDisplay = infoEnd.substring(0, 2) + "h" + infoEnd.substring(2);
            switch (infoStart) {
                case "OPE":
                    tvOpenTime.setText(STATUS_OPEN);
                    break;
                case "CLO":
                    tvOpenTime.setText(STATUS_CLOSED);
                    break;
                case "OP*":
                    tvOpenTime.setText(STATUS_OPEN247);
                    break;
                case "OPD":
                    tvOpenTime.setText(STATUS_OPEN24);
                    break;
                case "OPU":
                    tvOpenTime.setText(STATUS_OPEN_UNTIL + infoToDisplay);
                    break;
                case "OPA":
                    tvOpenTime.setText(STATUS_OPEN_AT + infoToDisplay);
                    break;
                case "???":
                    tvOpenTime.setText(STATUS_UNKNOWN);
                    break;
                default:
                    tvOpenTime.setText(information);
                    break;
            }
        } else {
            tvOpenTime.setText(STATUS_UNKNOWN);
        }
    }

    private void setupTextScrolling() {
        // Set TextView selected for text scrolling
        List<TextView> tvFields = Arrays.asList(tvTitle, tvAddress);
        for (TextView tvField : tvFields) {
            tvField.setSelected(true);
        }
    }

}
