package com.example.go4lunch.view;

import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.RestaurantListItemBinding;
import com.example.go4lunch.model.Restaurant;


import java.util.Arrays;
import java.util.List;

public class ListViewViewHolder extends RecyclerView.ViewHolder {

    private final TextView tvTitle;
    private final TextView tvDistance;
    private final TextView tvCountry;
    private final TextView tvAddress;
    private final TextView tvWorkmatesCount;
    private final TextView tvOpenTime;
    private final ImageView ivWorkmateLogo;
    private final RatingBar mRatingBar;
    private final ImageView ivPicture;


    public ListViewViewHolder(@NonNull View itemView) {
        super(itemView);
        RestaurantListItemBinding binding = RestaurantListItemBinding.bind(itemView);
        tvTitle = binding.restaurantItemTitle;
        tvDistance = binding.restaurantItemDistance;
        tvCountry = binding.restaurantItemCountry;
        tvAddress = binding.restaurantItemAddress;
        ivWorkmateLogo = binding.restaurantItemWorkmateLogo;
        tvWorkmatesCount = binding.restaurantItemWorkmatesCount;
        tvOpenTime = binding.restaurantItemOpenTime;
        mRatingBar = binding.restaurantItemRatingBar;
        ivPicture = binding.restaurantItemPicture;

        /*  // TODO : to be deleted cause replaced with ViewBinding
        tvTitle = itemView.findViewById(R.id.restaurant_item_title);
        tvDistance = itemView.findViewById(R.id.restaurant_item_distance);
        tvCountry = itemView.findViewById(R.id.restaurant_item_country);
        tvAddress = itemView.findViewById(R.id.restaurant_item_address);
        ivWorkmateLogo = itemView.findViewById(R.id.restaurant_item_workmate_logo);
        tvWorkmatesCount = itemView.findViewById(R.id.restaurant_item_workmates_count);
        tvOpenTime = itemView.findViewById(R.id.restaurant_item_openTime);
        mRatingBar = itemView.findViewById(R.id.restaurant_item_rating_bar);
        ivPicture = itemView.findViewById(R.id.restaurant_item_picture);
        */
    }

    public void updateWithRestaurants(Restaurant restaurant, String KEY) {
        // Display restaurant name
        tvTitle.setText(restaurant.getName());
        // Display restaurant distance
        String distance = restaurant.getDistance() + " m";
        tvDistance.setText(distance);
        // Display restaurant country                                                       // TODO
        // tvCountry.setText(restaurant.getNationality());
        tvCountry.setText("Frenchy");
        // Display restaurant address
        tvAddress.setText(restaurant.getAddress());
        // Display workmate logo
        ivWorkmateLogo.setImageResource(R.drawable.ic_outline_person_black_24);
        // Display workmates count
        String workmatesCount = "(" + restaurant.getLikesCount() + ")";
        tvWorkmatesCount.setText(workmatesCount);
        // Display restaurant opening time                                                  // TODO
        //tvOpenTime.setText(restaurant.getOpeningHours().isOpenNow()? R.string.status_open:R.string.status_closed);
        tvOpenTime.setText("It may be open... or not !");
        // Display restaurant rating
        mRatingBar.setRating((float) restaurant.getRating());
        // Display restaurant picture                                                       // TODO
        ivPicture.setImageResource(R.drawable.im_detail_restaurant);
        // ivPicture.setImageResource(restaurant.getPhotos().get(0).getPhotoUrl(KEY));

        // Set text scrolling and adapt fields max size in line 2
        ResizeAndScroll();
    }

    private void ResizeAndScroll() {

        ////////////
        // Resize //
        ////////////

        // Adjust max sizes in line 2 according to each text length
        final int TEXT_SIZE_FACTOR = 8; // Size of a character in dp
        final int RESIZING_LENGTH_LIMIT = 16;   // Limit for tvCountry to allow resizing
        // Convert width in px into width in dp and apply TEXT_SIZE_FACTOR to convert width in dp into length
        int tvCountryDefaultMaxTextLength = (int) ((tvCountry.getMaxWidth() / tvCountry.getContext().getResources().getDisplayMetrics().density + 0.5f) / TEXT_SIZE_FACTOR) - 1;
        int tvAddressDefaultMaxTextLength = (int) ((tvAddress.getMaxWidth() / tvAddress.getContext().getResources().getDisplayMetrics().density + 0.5f) / TEXT_SIZE_FACTOR) - 1;
        int tvCountryTextLength = tvCountry.getText().length();
        // Calculate length delta
        int deltaLength = tvCountryTextLength - tvCountryDefaultMaxTextLength;

        // Allow resizing only if new length is shorter than the new length limit
        if( deltaLength > 0 && tvCountryTextLength <= RESIZING_LENGTH_LIMIT) {

            // Increase tvCountry max width by the delta to fit better or totally to the text
            // Convert length into width in dp
            int newTvCountryMaxWidthInDp = (tvCountryTextLength + 1) * TEXT_SIZE_FACTOR;
            // Convert width in dp into width in px
            int newTvCountryMaxWidthInPx = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newTvCountryMaxWidthInDp, tvCountry.getResources().getDisplayMetrics()));
            // Set new width max
            tvCountry.setMaxWidth(newTvCountryMaxWidthInPx);

            // Decrease tvAddress max width by the same delta so as not to overflow the screen
            // Convert length into width in dp
            int newTvAddressMaxWidthInDp = (tvAddressDefaultMaxTextLength - deltaLength + 1) * TEXT_SIZE_FACTOR;
            // Convert width in dp into width in px
            int newTvAddressMaxWidthInPx = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newTvAddressMaxWidthInDp, tvAddress.getResources().getDisplayMetrics()));
            // Set new width max
            tvAddress.setMaxWidth(newTvAddressMaxWidthInPx);
        }

        ////////////
        // Scroll //
        ////////////

        // Set TextView selected for text scrolling
        List<TextView> fields = Arrays.asList(tvTitle, tvCountry, tvAddress, tvOpenTime);
        for (TextView field : fields) {
            field.setSelected(true);
        }
    }

}
