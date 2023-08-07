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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentDetailRestaurantBinding;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.model.api.model.Photo;
import com.example.go4lunch.utils.EventObjectClick;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.view.adapter.DetailRestaurantWorkmateAdapter;
import com.example.go4lunch.viewmodel.DetailRestaurantViewModel;
import com.example.go4lunch.viewmodel.ViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailRestaurantFragment extends Fragment implements View.OnClickListener {

    private FragmentDetailRestaurantBinding binding;
    private OnButtonClickedListener mCallback;
    private DetailRestaurantWorkmateAdapter detailRestaurantWorkmateAdapter;
    private DetailRestaurantViewModel detailRestaurantViewModel;

    // Constructor
    public DetailRestaurantFragment() {
        // Required empty public constructor
    }

    public static DetailRestaurantFragment newInstance() {
        return (new DetailRestaurantFragment());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDetailRestaurantBinding.inflate(inflater, container, false);
        // Initialize ViewModel
        detailRestaurantViewModel = new ViewModelProvider(requireActivity(), new ViewModelFactory()).get(DetailRestaurantViewModel.class);
        // Set onClickListener to buttons
        setListeners();
        // Initialize RecyclerView
        configureRecyclerView();
        configureOnClickRecyclerView();
        // Get data from calling activity, get details from API and display all
        getAndDisplayData();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    // Spread the click to the parent activity
    public void onClick(View v) {
        RestaurantWithDistance restaurant = detailRestaurantViewModel.getRestaurant();
        String message = null;
        switch (EventObjectClick.fromView(v)) {
            case BTN_CALL:
            case BTN_WEBSITE:
                break;
            case BTN_LIKE:
                message = (detailRestaurantViewModel.updateLikedRestaurant(restaurant.getRid())) ?
                        getString(R.string.btn_like_checked) : getString(R.string.btn_like_unchecked);
                break;
            case FAB_SELECT:
                message = (detailRestaurantViewModel.updateSelection(restaurant.getRid(), restaurant.getName(), restaurant.getAddress())) ?
                        getString(R.string.fab_checked) : getString(R.string.fab_unchecked);
                break;
        }
        mCallback.onButtonClicked(v, message, restaurant.getPhoneNumber(), restaurant.getWebsite());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Call the method creating callback after being attached to parent activity
        this.createCallbackToParentActivity();
    }

    // Set onClickListener to buttons
    private void setListeners() {
        binding.fabSelection.setOnClickListener(this);
        binding.buttonCall.setOnClickListener(this);
        binding.buttonLike.setOnClickListener(this);
        binding.buttonWebsite.setOnClickListener(this);
    }

    // Declare an interface that will be implemented by any container activity for callback
    public interface OnButtonClickedListener {
        void onButtonClicked(View view, String message, String rPhoneNumber, String rWebsite);
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
        detailRestaurantWorkmateAdapter = new DetailRestaurantWorkmateAdapter(detailRestaurantViewModel.getSelectors(), getString(R.string.text_joining));
        // Attach the adapter to the recyclerview to populate items
        binding.rvDetailRestaurant.setAdapter(detailRestaurantWorkmateAdapter);
        // Set layout manager to position the items
        binding.rvDetailRestaurant.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(binding.rvDetailRestaurant, R.layout.workmate_list_item)
                .setOnItemClickListener((recyclerView, position, v) -> Log.w("TAG", "Position : "+position));
    }

    // Get restaurant from calling activity
    private void getAndDisplayData() {
        Intent intent = requireActivity().getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                RestaurantWithDistance restaurant = (RestaurantWithDistance) bundle.getSerializable("RESTAURANT");
                // Init and display data
                if (restaurant != null) initData(restaurant);
                Log.w("DetailRestaurantFragment", "Name of this restaurant : " + restaurant.getName());
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initData(RestaurantWithDistance restaurant) {
        // Initialize restaurant data
        initAndDisplayRestaurantData(restaurant);
        // Initialize selectors list
        initSelectors(restaurant.getRid());
        // Initialize liked restaurants list
        initLikedRestaurants(restaurant.getRid());
    }

    private void initAndDisplayRestaurantData(RestaurantWithDistance restaurant) {
        // Get restaurant details
        detailRestaurantViewModel.fetchRestaurantDetails(restaurant, getString(R.string.MAPS_API_KEY));
        detailRestaurantViewModel.getRestaurantDetailsMutableLiveData().observe(requireActivity(), restaurantWithDetails -> {
            displayRestaurantData(restaurantWithDetails);
            detailRestaurantViewModel.setRestaurant(restaurantWithDetails);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initSelectors(String rId) {
        detailRestaurantViewModel.getWorkmatesMutableLiveData().observe(requireActivity(), workmates -> {
            List<User> selectorsList = detailRestaurantViewModel.getSelectors(rId, workmates);
            updateSelectionFab(detailRestaurantViewModel.checkCurrentUserSelection(selectorsList));

            detailRestaurantWorkmateAdapter.notifyDataSetChanged();

            // Configure empty selectors list message visibility according to selectors list
            int emptyListMessageVisibility = (selectorsList.isEmpty()) ? View.VISIBLE : View.GONE;
            binding.messageEmptyList.setVisibility(emptyListMessageVisibility);
        });
    }

    private void initLikedRestaurants(String rId) {
        detailRestaurantViewModel.getLikedRestaurantsMutableLiveData().observe(requireActivity(), likedRestaurantsList -> {
            boolean isLiked = (detailRestaurantViewModel.checkCurrentUserLikes(rId, likedRestaurantsList));
            updateLikeButton(isLiked);
        });
    }

    private void displayRestaurantData(RestaurantWithDistance restaurantWithDetails) {
        // Nearby API data
        List<Photo> rPhotos = restaurantWithDetails.getPhotos();
        if (rPhotos != null) Picasso.get().load(rPhotos.get(0).getPhotoUrl(getString(R.string.MAPS_API_KEY))).into(binding.ivRestaurant);
        binding.tv1Restaurant.setText(restaurantWithDetails.getName());
        binding.ratingBarRestaurant.setRating((float) (restaurantWithDetails.getRating() * 3/5));
        // Place Details API data
        binding.tv2Restaurant.setText(restaurantWithDetails.getAddress());
        binding.tv3Restaurant.setText(detailRestaurantViewModel.getOpeningInformation(restaurantWithDetails, requireContext()));
    }

    @SuppressLint("UseCompatTextViewDrawableApis")  // For the use of setCompoundDrawableTintList()
    private void updateLikeButton(boolean isLiked) {
        // Define the foreground of the like button, according to the like status
        int colorId = (isLiked) ? R.color.yellow_ic : R.color.app_background;
        int backgroundColorId = (isLiked) ? R.color.app_background : R.color.white;
        // Set up new colors of the like button
        binding.buttonLike.setCompoundDrawableTintList(AppCompatResources.getColorStateList(requireContext(), colorId));
        binding.buttonLike.setTextColor(getResources().getColor(colorId, requireContext().getTheme()));
        binding.buttonLike.setBackgroundColor(getResources().getColor(backgroundColorId, requireContext().getTheme()));
    }

    private void updateSelectionFab(boolean isSelected) {
        // Define the foreground of the selection FAB mipmap, according to the selection status
        int resId = (isSelected) ? R.mipmap.im_check_green_white : R.mipmap.im_check_grey_white;
        // Set up the new foreground of the selection FAB
        binding.fabSelection.setForeground(AppCompatResources.getDrawable(requireContext(),resId));
    }

}