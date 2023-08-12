package com.example.go4lunch.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.view.viewholder.ListViewViewHolder;
import com.example.go4lunch.viewmodel.ListViewViewModel;

import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewViewHolder> {

    private final List<RestaurantWithDistance> restaurantsList;
    private final List<User> workmatesList;
    private final Context context;

    // Constructor
    public ListViewAdapter(List<RestaurantWithDistance> restaurantsList, List<User> workmatesList, Context context) {
        this.restaurantsList = restaurantsList;
        this.workmatesList = workmatesList;
        this.context = context;

    }


    @NonNull
    @Override
    public ListViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create view holder and inflate its layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.restaurant_list_item, parent, false);

        return new ListViewViewHolder(view);
    }


    // Update view holder with restaurants
    @Override
    public void onBindViewHolder(@NonNull ListViewViewHolder viewHolder, int position) {
        ListViewViewModel listViewViewModel = new ListViewViewModel();

        RestaurantWithDistance restaurant = restaurantsList.get(position);
        viewHolder.updateWithRestaurants(restaurant.getName(), restaurant.getRating(),
                listViewViewModel.getDistance(restaurant.getDistance()),
                listViewViewModel.countSelections(restaurant.getRid(), workmatesList),
                listViewViewModel.getOpeningInfo(restaurant.getOpeningHours(), context),
                listViewViewModel.getPhotoUrl(restaurant.getPhotos(), context));
    }

    // Return the total count of restaurants
    @Override
    public int getItemCount() {
        return this.restaurantsList.size();
    }

}
