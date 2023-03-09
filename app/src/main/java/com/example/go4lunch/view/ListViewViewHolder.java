package com.example.go4lunch.view;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.RestaurantListItemBinding;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.CalendarUtils;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.example.go4lunch.utils.FirestoreUtils;
import com.example.go4lunch.utils.MapsApisUtils;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ListViewViewHolder extends RecyclerView.ViewHolder {

    private final TextView tvTitle;
    private final TextView tvDistance;
    private final TextView tvCountry;
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
        tvCountry = binding.restaurantItemCountry;
        tvAddress = binding.restaurantItemAddress;
        ivWorkmateLogo = binding.restaurantItemWorkmateLogo;
        tvSelectionsCount = binding.restaurantItemSelectionsCount;
        tvOpenTime = binding.restaurantItemOpenTime;
        mRatingBar = binding.restaurantItemRatingBar;
        ivPicture = binding.restaurantItemPicture;
    }

    public void updateWithRestaurants(Restaurant restaurant, String KEY, String STATUS_OPEN, String STATUS_CLOSED,
                                      String STATUS_OPEN247, String STATUS_OPEN_UNTIL, String STATUS_OPEN_AT) {

        // Display restaurant name
        tvTitle.setText(restaurant.getName());
        // Display restaurant distance
        // String distance = restaurant.getDistance() + "m";    // TODO : To be deleted
        String distance = DataProcessingUtils.calculateRestaurantDistance(restaurant, MapsApisUtils.getHome()) + "m";
        tvDistance.setText(distance);
        // Display restaurant country                                                       // TODO
        // tvCountry.setText(restaurant.getNationality());
        tvCountry.setText("Frenchy");
        // Display restaurant address
        tvAddress.setText(restaurant.getAddress());
        // Display workmate logo
        ivWorkmateLogo.setImageResource(R.drawable.ic_outline_person_black_24);
        // Display selections count
        displaySelectionsCount(restaurant.getId());
        // Display restaurant opening info
        String information = restaurant.getOpeningInformation();
        if (information != "") {
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
                case "OPU":
                    tvOpenTime.setText(STATUS_OPEN_UNTIL + infoToDisplay);
                    break;
                case "OPA":
                    tvOpenTime.setText(STATUS_OPEN_AT + infoToDisplay);
                    break;
                default:
                    tvOpenTime.setText(restaurant.getOpeningInformation());
                    break;
            }
        } else {
            tvOpenTime.setText("");
        }
        // Display restaurant rating
        mRatingBar.setRating((float) (restaurant.getRating() * 3/5));
        // Display restaurant picture
        // ivPicture.setImageResource(R.drawable.im_detail_restaurant);
        if (restaurant.getPhotos() != null) Picasso.get().load(restaurant.getPhotos().get(0).getPhotoUrl(KEY)).into(ivPicture);
        // Set text scrolling and adapt fields max size in line 2
        ResizeAndScroll();
    }

    private void displaySelectionsCount(String rId) {
        // Get workmates list
        UserManager.getUsersList(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    int selectionsCount = 0;
                    // Get users list
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> userData = document.getData(); // TODO : Map data for debug. To be deleted
                        // Get workmate in workmates list
                        User workmate = FirestoreUtils.getUserFromDatabaseDocument(document);
                        // Check selected restaurant id and date and increase selections count if matches with restaurant id
                        boolean isSelected = rId.equals(workmate.getSelectionId())
                                        && currentDate.equals(workmate.getSelectionDate());
                        if (isSelected) selectionsCount += 1;
                    }
                    // Display selections count
                    if (selectionsCount >0) {
                        ivWorkmateLogo.setVisibility(View.VISIBLE);
                        tvSelectionsCount.setVisibility(View.VISIBLE);
                    } else {
                        ivWorkmateLogo.setVisibility(View.INVISIBLE);
                        tvSelectionsCount.setVisibility(View.INVISIBLE);
                    }
                    String workmatesCount = "(" + selectionsCount + ")";
                    tvSelectionsCount.setText(workmatesCount);
                }
            } else {
                Log.w("ListViewViewHolder", "Error getting documents: ", task.getException());
            }
        });
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
