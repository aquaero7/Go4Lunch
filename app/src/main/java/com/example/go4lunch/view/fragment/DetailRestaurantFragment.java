package com.example.go4lunch.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentDetailRestaurantBinding;
import com.example.go4lunch.model.model.LikedRestaurant;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.model.api.model.Photo;
import com.example.go4lunch.utils.EventButtonClick;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.view.adapter.DetailRestaurantWorkmateAdapter;
import com.example.go4lunch.viewmodel.DetailRestaurantViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailRestaurantFragment extends Fragment implements View.OnClickListener {

    // Declare callback
    private OnButtonClickedListener mCallback;
    // Declare ViewBinding
    private FragmentDetailRestaurantBinding binding;

    // Declare View items
    private RecyclerView mRecyclerView;
    private DetailRestaurantWorkmateAdapter detailRestaurantWorkmateAdapter;
    private ImageView mImageView;
    private TextView mTextView1;
    private RatingBar mRatingBar;
    private TextView mTextView2;
    private FloatingActionButton mSelectionFab;
    private TextView mEmptyListMessage;
    private Button mCallButton;
    private Button mLikeButton;
    private Button mWebsiteButton;

    // Declare restaurant
    private RestaurantWithDistance restaurant;
    private String rId, rName, rAddress, rPhotoUrl;
    private List<Photo> rPhotos;
    private double rRating;
    private boolean isSelected;
    private boolean isLiked;

    // Declare ViewModel
    DetailRestaurantViewModel detailRestaurantViewModel;

    // Initialize current user
    private User currentUser;

    // Declare and initialize list
    private List<User> workmatesList = new ArrayList<>();
    private List<User> selectorsList = new ArrayList<>();
    private List<LikedRestaurant> likedRestaurantsList = new ArrayList<>();

    // Declare and initialize Google Maps API key
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
        mImageView = binding.ivRestaurant;
        mTextView1 = binding.tv1Restaurant;
        mTextView2 = binding.tv2Restaurant;
        mRatingBar = binding.ratingBarRestaurant;
        mSelectionFab = binding.fabSelection;
        mEmptyListMessage = binding.messageEmptyList;
        mCallButton = binding.buttonCall;
        mLikeButton = binding.buttonLike;
        mWebsiteButton = binding.buttonWebsite;

        //Set onClickListener to selection fab
        mSelectionFab.setOnClickListener(this);
        // Set onClickListener to buttons
        mCallButton.setOnClickListener(this);
        mLikeButton.setOnClickListener(this);
        mWebsiteButton.setOnClickListener(this);

        // Initialize RecyclerView
        configureRecyclerView();
        configureOnClickRecyclerView();

        // Initialize ViewModel
        detailRestaurantViewModel = new ViewModelProvider(requireActivity()).get(DetailRestaurantViewModel.class);

        // Get data from calling activity
        getIntentData();

        // Initialize API key
        KEY = getString(R.string.MAPS_API_KEY);

        // Initialize data and display restaurant data
        if (restaurant != null) {
            initData();
            displayRestaurantData();
        }

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    /* Spread the click to the parent activity
    Binding added as an argument to make it available in the activity */
    public void onClick(View v) {
        switch (EventButtonClick.from(v)) {
            case BTN_CALL:
            case BTN_WEBSITE:
                break;
            case BTN_LIKE:
                isLiked = !isLiked;
                updateLikeButton();
                break;
            case FAB_SELECT:
                isSelected = !isSelected;
                updateSelectionFab();
                // updateLocalObjectsWithSelection();
                break;
        }
        mCallback.onButtonClicked(v, binding, restaurant.getRid(), restaurant.getName(),
                restaurant.getAddress(), restaurant.getPhoneNumber(), restaurant.getWebsite(),
                restaurant.getRating(), restaurant.getPhotos(), isSelected, isLiked);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Call the method creating callback after being attached to parent activity
        this.createCallbackToParentActivity();
    }

    /* Declare an interface that will be implemented by any container activity for callback
    Binding added as an argument to make it available in the activity */
    public interface OnButtonClickedListener {
        void onButtonClicked(View view, FragmentDetailRestaurantBinding binding, String rId, String rName,
                             String rAddress, String rPhoneNumber, String rWebsite, double rRating,
                             List<Photo> rPhotos, boolean isSelected, boolean isLiked);
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
        // Declare and create adapter
        detailRestaurantWorkmateAdapter = new DetailRestaurantWorkmateAdapter(selectorsList, getString(R.string.text_joining));
        // Attach the adapter to the recyclerview to populate items
        mRecyclerView.setAdapter(detailRestaurantWorkmateAdapter);
        // Set layout manager to position the items
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView, R.layout.workmate_list_item)
                .setOnItemClickListener((recyclerView, position, v) -> Log.w("TAG", "Position : "+position));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initData() {
        // Initialize restaurant data
        rId = restaurant.getRid();
        rName = restaurant.getName();
        rAddress = restaurant.getAddress();
        rPhotos = restaurant.getPhotos();
        rRating = restaurant.getRating();
        if (rPhotos != null) rPhotoUrl = rPhotos.get(0).getPhotoUrl(KEY);

        // Initialize selectors list
        selectorsList.clear();
        selectorsList.addAll(detailRestaurantViewModel.getSelectors(rId, workmatesList));

        detailRestaurantWorkmateAdapter.notifyDataSetChanged();

        // Configure empty selectors list message visibility according to selectors list
        int emptyListMessageVisibility = (selectorsList.isEmpty()) ? View.VISIBLE : View.GONE;
        mEmptyListMessage.setVisibility(emptyListMessageVisibility);
    }

    // Get restaurant from calling activity
    private void getIntentData() {
        Intent intent = requireActivity().getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                restaurant = (RestaurantWithDistance) bundle.getSerializable("RESTAURANT");
                currentUser = (User) bundle.getSerializable("CURRENT_USER");
                workmatesList = (List<User>) bundle.getSerializable("WORKMATES");
                likedRestaurantsList = (List<LikedRestaurant>) bundle.getSerializable("LIKED_RESTAURANTS");
                Log.w("DetailRestaurantFragment", "Name of this restaurant : " + restaurant.getName());
            }
        }
    }

    private void displayRestaurantData() {
        if (rPhotos != null) Picasso
                .get()
                .load(rPhotoUrl).into(mImageView);
        mTextView1.setText(rName);
        mTextView2.setText(rAddress);
        mRatingBar.setRating((float) (rRating * 3/5));

        isLiked = (detailRestaurantViewModel.checkCurrentUserLikes(currentUser, likedRestaurantsList));
        updateLikeButton();

        isSelected = (detailRestaurantViewModel.checkCurrentUserSelection(currentUser, rId));
        updateSelectionFab();
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

    private void updateSelectionFab() {
        // Define the foreground of the selection FAB mipmap, according to the selection status
        int resId = (isSelected) ? R.mipmap.im_check_green_white : R.mipmap.im_check_grey_white;
        // Set up the new foreground of the selection FAB
        mSelectionFab.setForeground(AppCompatResources.getDrawable(requireContext(),resId));
    }

    /*
    private void updateLocalObjectsWithSelection() {
        String selIdUpdate, selDateUpdate, selNameUpdate, selAddressUpdate;
        if (isSelected) {
            selIdUpdate = rId;
            selDateUpdate = currentDate;
            selNameUpdate = rName;
            selAddressUpdate = rAddress;
        } else {
            selIdUpdate = null;
            selDateUpdate = null;
            selNameUpdate = null;
            selAddressUpdate = null;
        }
        userManager.getCurrentUserData()
                .addOnSuccessListener(currentUser -> {
                    // Update current user
                    currentUser.setSelectionId(selIdUpdate);
                    currentUser.setSelectionDate(selDateUpdate);
                    currentUser.setSelectionName(selNameUpdate);
                    currentUser.setSelectionAddress(selAddressUpdate);
                    // Update workmates list
                    for (User workmate : workmatesList) {
                        if (currentUser.getUid().equals(workmate.getUid())) {
                            workmate.setSelectionId(selIdUpdate);
                            workmate.setSelectionDate(selDateUpdate);
                            workmate.setSelectionName(selNameUpdate);
                            workmate.setSelectionAddress(selAddressUpdate);
                            break;
                        }
                    }
                    initData(); // To update recyclerView with local data updated
                })
                .addOnFailureListener(e -> {
                    Log.w("DetailRestaurantFragment", e.getMessage());
                });
    }
    */

}