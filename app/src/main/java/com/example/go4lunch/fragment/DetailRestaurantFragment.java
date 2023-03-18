package com.example.go4lunch.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentDetailRestaurantBinding;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.Photo;
import com.example.go4lunch.utils.CalendarUtils;
import com.example.go4lunch.utils.FirestoreUtils;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.view.DetailRestaurantWorkmateAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailRestaurantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailRestaurantFragment extends Fragment implements View.OnClickListener {

    // Declare callback
    private OnButtonClickedListener mCallback;
    // Declare ViewBinding
    private FragmentDetailRestaurantBinding binding;
    // Declare View items
    private RecyclerView mRecyclerView;
    private ImageView mImageView;
    private TextView mTextView1;
    private RatingBar mRatingBar;
    private TextView mTextView2;
    private FloatingActionButton mSelectionFab;
    private TextView mEmptyListMessage;
    private Button mCallButton;
    private Button mLikeButton;
    private Button mWebsiteButton;
    // Declare current user id
    private String uid;
    // Declare restaurant
    private Restaurant restaurant;
    private String rid;
    // Declare current date
    private final String currentDate = CalendarUtils.getCurrentDate();
    // Declare selected restaurant
    private String selectionId;
    private String selectionDate;
    private boolean isSelected;
    private boolean isLiked;
    // Declare Workmates-Selectors list
    private List<User> selectorsList;
    // Declare Workmate-Selector to add to create Workmates-Selectors list
    private User selectorToAdd;
    // Declare liked restaurants list
    private List<LikedRestaurant> likedRestaurantsList;
    // Initialize Google Maps API key
    private String KEY;

    // Constructor
    public DetailRestaurantFragment() {
        // Required empty public constructor
    }

    public static DetailRestaurantFragment newInstance() {
        return (new DetailRestaurantFragment());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDetailRestaurantBinding.inflate(inflater, container, false);

        // Initialize View items
        mRecyclerView = binding.rvDetailRestaurant;
        mImageView = binding.restaurantIv;
        mTextView1 = binding.restaurantTv1;
        mTextView2 = binding.restaurantTv2;
        mRatingBar = binding.restaurantRatingBar;
        mSelectionFab = binding.selectionFab;
        mEmptyListMessage = binding.emptyListMessage;
        mCallButton = binding.callButton;
        mLikeButton = binding.likeButton;
        mWebsiteButton = binding.websiteButton;

        // Get restaurant from calling activity
        getIntentData();
        if (restaurant != null) displayRestaurantData();

        //Set onClickListener to selection fab
        mSelectionFab.setOnClickListener(this);
        // Set onClickListener to buttons
        mCallButton.setOnClickListener(this);
        mLikeButton.setOnClickListener(this);
        mWebsiteButton.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getSelectorsListAndConfigureRecyclerView();
    }

    @Override
    /* Spread the click to the parent activity
    Binding added as an argument to make it available in the activity */
    public void onClick(View v) {
        String tag = String.valueOf(v.getTag());
        switch (tag) {
            case "BTN_CALL":
            case "BTN_WEBSITE":
                break;
            case "BTN_LIKE":
                isLiked = !isLiked;
                updateLikeButton();
                break;
            case "FAB":
                isSelected = !isSelected;
                updateSelectionFab();
                getSelectorsListAndConfigureRecyclerView();
                break;
        }
        mCallback.onButtonClicked(v, binding, restaurant.getId(), restaurant.getName(),
                restaurant.getAddress(), restaurant.getPhoneNumber(), restaurant.getWebsite(),
                restaurant.getRating(), restaurant.getPhotos(), isSelected, isLiked, uid);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Call the method creating callback after being attached to parent activity
        this.createCallbackToParentActivity();
        KEY  = getString(R.string.MAPS_API_KEY);
    }


    /* Declare an interface that will be implemented by any container activity for callback
    Binding added as an argument to make it available in the activity */
    public interface OnButtonClickedListener {
        void onButtonClicked(View view, FragmentDetailRestaurantBinding binding, String rId, String rName,
                             String rAddress, String rPhoneNumber, String rWebsite, double rRating,
                             List<Photo> rPhotos, boolean isSelected, boolean isLiked, String uId);
    }

    // Create callback to parent activity
    private void createCallbackToParentActivity(){
        try {
            //Parent activity will automatically subscribe to callback
            mCallback = (OnButtonClickedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e + " must implement OnButtonClickedListener");
        }
    }

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {
        // Configure empty selectors list message visibility according to selectors list
        int emptyListMessageVisibility = (selectorsList.isEmpty()) ? View.VISIBLE : View.GONE;
        mEmptyListMessage.setVisibility(emptyListMessageVisibility);
        // Declare and create adapter
        DetailRestaurantWorkmateAdapter detailRestaurantWorkmateAdapter =
                new DetailRestaurantWorkmateAdapter(selectorsList, getString(R.string.joining_text));
        // Attach the adapter to the recyclerview to populate items
        mRecyclerView.setAdapter(detailRestaurantWorkmateAdapter);
        // Set layout manager to position the items
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView, R.layout.workmate_list_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.w("TAG", "Position : "+position);
                    }
                });
    }

    private void getSelectorsListAndConfigureRecyclerView() {
        String restaurantId = restaurant.getId();
        selectorsList = new ArrayList<>();
        UserManager.getUsersList(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    // Check selected restaurant id and date and get users list
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> userData = document.getData(); // TODO : Map data for debug.
                        selectorToAdd = FirestoreUtils.getUserFromDatabaseDocument(document);
                        boolean isSelector = (restaurantId.equals(selectorToAdd.getSelectionId())
                                        && currentDate.equals(selectorToAdd.getSelectionDate()));
                        if (isSelector) selectorsList.add(selectorToAdd);
                    }
                }
            } else {
                Log.w("DetailRestaurantFragment", "Error getting documents: ", task.getException());
            }
            configureRecyclerView();
            configureOnClickRecyclerView();
        });
    }

    // Get restaurant from calling activity
    private void getIntentData() {
        Intent intent = requireActivity().getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                restaurant = (Restaurant) bundle.getSerializable("RESTAURANT");
                Log.w("DetailRestaurantFragment", "Name of this restaurant : " + restaurant.getName());
            }
        }
    }

    private void displayRestaurantData() {
        if (restaurant.getPhotos() != null) Picasso
                .get()
                .load(restaurant.getPhotos().get(0).getPhotoUrl(KEY)).into(mImageView);
        mTextView1.setText(restaurant.getName());
        mTextView2.setText(restaurant.getAddress());
        mRatingBar.setRating((float) (restaurant.getRating() * 3/5));
        setupSelectionFab();
        setupLikeButton();
    }

    private void setupSelectionFab() {
        // Get current user selected restaurant id from database
        UserManager.getInstance().getCurrentUserData().addOnSuccessListener(user -> {
            // Get current user selected restaurant id from database
            selectionId = user.getSelectionId();
            selectionDate = user.getSelectionDate();
            // Get the id of this restaurant
            String restaurantId = restaurant.getId();
            // Update selection status
            isSelected = (restaurantId.equals(selectionId)) && (currentDate.equals(selectionDate));

            updateSelectionFab();
        });
    }

    private void updateSelectionFab() {
        // Define the foreground of the selection FAB mipmap, according to the selection status
        int resId = (isSelected) ? R.mipmap.im_check_green_white : R.mipmap.im_check_grey_white;
        // Set up the new foreground of the selection FAB
        mSelectionFab.setForeground(AppCompatResources.getDrawable(requireContext(),resId));
    }

    private void setupLikeButton() {
        // Check in database if this restaurant is liked by current user
        rid = restaurant.getId();
        uid = UserManager.getInstance().getCurrentUserId();
        likedRestaurantsList = FirestoreUtils.getLikedRestaurantsList();
        // Update like status
        isLiked = false;
        if (likedRestaurantsList != null) {
            for (LikedRestaurant likedRestaurant : likedRestaurantsList) {
                if (rid.equals(likedRestaurant.getRid()) && uid.equals(likedRestaurant.getUid())) {
                    isLiked = true;
                    break;
                }
            }
        }
        updateLikeButton();
    }

    @SuppressLint("UseCompatTextViewDrawableApis")  // For the use of setCompoundDrawableTintList()
    private void updateLikeButton() {
        // Define the foreground of the like button, according to the like status
        int colorId = (isLiked) ? R.color.yellow_ic : R.color.app_background;
        int backgroundColorId = (isLiked) ? R.color.app_background : R.color.white;
        // Set up new colors of the like button
        mLikeButton.setCompoundDrawableTintList(AppCompatResources.getColorStateList(requireContext(), colorId));
        mLikeButton.setTextColor(getResources().getColor(colorId, requireContext().getTheme()));
        mLikeButton.setBackgroundColor(getResources().getColor(backgroundColorId, requireContext().getTheme()));
    }

}