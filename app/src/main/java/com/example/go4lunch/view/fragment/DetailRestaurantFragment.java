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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private TextView mTextView3;
    private FloatingActionButton mSelectionFab;
    private TextView mEmptyListMessage;
    private Button mCallButton;
    private Button mLikeButton;
    private Button mWebsiteButton;

    // Declare restaurant
    private RestaurantWithDistance restaurant;
    private String rId, rName, rAddress, rOpeningInfo, rPhotoUrl;
    private List<Photo> rPhotos;
    private double rRating;
    private boolean isSelected;
    private boolean isLiked;

    // Declare ViewModel
    DetailRestaurantViewModel detailRestaurantViewModel;

    // Initialize current user
    private User currentUser;

    // Declare and initialize list
    private List<User> selectorsList = new ArrayList<>();
    private List<LikedRestaurant> likedRestaurantsList = new ArrayList<>();

    // Declare Google Maps API key
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
        mTextView3 = binding.tv3Restaurant;
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

        // Initialize the list of opening information to display
        initInfoList();

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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    /* Spread the click to the parent activity
    Binding added as an argument to make it available in the activity */
    public void onClick(View v) {
        switch (EventButtonClick.from(v)) {
            case BTN_CALL:
            case BTN_WEBSITE:
            case BTN_LIKE:
            case FAB_SELECT:
                // Actions are managed in activity. Useless with livedata.
                break;
        }
        mCallback.onButtonClicked(v, binding, restaurant.getRid(), restaurant.getName(),
                restaurant.getAddress(), restaurant.getPhoneNumber(), restaurant.getWebsite(),
                restaurant.getRating(), restaurant.getPhotos(), !isSelected, !isLiked);
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

    // Initialize the list of opening information to display
    private void initInfoList() {
        Map<String, String> infoList = new HashMap<>();
        infoList.put("OPE", getString(R.string.status_open));
        infoList.put("CLO", getString(R.string.status_closed));
        infoList.put("OP*", getString(R.string.status_open247));
        infoList.put("OPD", getString(R.string.status_open24));
        infoList.put("OPU", getString(R.string.status_open_until));
        infoList.put("OPA", getString(R.string.status_open_at));
        infoList.put("???", getString(R.string.status_unknown));
        detailRestaurantViewModel.setInfoList(infoList);
    }

    // Get restaurant from calling activity
    private void getIntentData() {
        Intent intent = requireActivity().getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                restaurant = (RestaurantWithDistance) bundle.getSerializable("RESTAURANT");
                Log.w("DetailRestaurantFragment", "Name of this restaurant : " + restaurant.getName());
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initData() {
        // Initialize restaurant data
        initRestaurantData();
        // Initialize current user
        initCurrentUser();
        // Initialize selectors list
        initSelectors();
        // Initialize liked restaurants list
        initLikedRestaurants();
    }

    private void initRestaurantData() {
        rId = restaurant.getRid();
        rName = restaurant.getName();
        rPhotos = restaurant.getPhotos();
        rRating = restaurant.getRating();
        if (rPhotos != null) rPhotoUrl = rPhotos.get(0).getPhotoUrl(KEY);

        // Get restaurant details
        detailRestaurantViewModel.fetchRestaurantDetails(restaurant, KEY);
        detailRestaurantViewModel.getRestaurantDetailsMutableLiveData().observe(requireActivity(), restaurant -> {
            rAddress = restaurant.getAddress();
            rOpeningInfo = detailRestaurantViewModel.getOpeningInformation(restaurant);
            displayRestaurantDetailData();
        });
    }

    private void initCurrentUser() {
        currentUser = detailRestaurantViewModel.getCurrentUser();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initSelectors() {
        detailRestaurantViewModel.getWorkmatesMutableLiveData().observe(requireActivity(), workmates -> {
            selectorsList.clear();
            selectorsList.addAll(detailRestaurantViewModel.getSelectors(rId, workmates));
            isSelected = (detailRestaurantViewModel.checkCurrentUserSelection(rId));
            updateSelectionFab();

            detailRestaurantWorkmateAdapter.notifyDataSetChanged();
            // Configure empty selectors list message visibility according to selectors list
            int emptyListMessageVisibility = (selectorsList.isEmpty()) ? View.VISIBLE : View.GONE;
            mEmptyListMessage.setVisibility(emptyListMessageVisibility);
        });
    }

    private void initLikedRestaurants() {
        detailRestaurantViewModel.getLikedRestaurantsMutableLiveData().observe(requireActivity(), likedRestaurants -> {
            likedRestaurantsList.clear();
            likedRestaurantsList.addAll(likedRestaurants);
            isLiked = (detailRestaurantViewModel.checkCurrentUserLikes(rId, likedRestaurantsList));
            updateLikeButton();
        });
    }

    private void displayRestaurantData() {
        if (rPhotos != null) Picasso.get().load(rPhotoUrl).into(mImageView);
        mTextView1.setText(rName);
        mRatingBar.setRating((float) (rRating * 3/5));
    }

    private void displayRestaurantDetailData() {
        mTextView2.setText(rAddress);
        mTextView3.setText(rOpeningInfo);
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

}