package com.example.go4lunch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;

import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewViewHolder> {

    private final String KEY;
    private final String STATUS_OPEN;
    private final String STATUS_CLOSED;
    private final String STATUS_OPEN247;
    private final String STATUS_OPEN24;
    private final String STATUS_OPEN_UNTIL;
    private final String STATUS_OPEN_AT;
    private final String STATUS_UNKNOWN;

    private List<RestaurantWithDistance> restaurantsList;
    private List<User> workmatesList;


    // Constructor
    public ListViewAdapter(List<RestaurantWithDistance> restaurantsList, List<User> workmatesList, String KEY, String STATUS_OPEN,
                           String STATUS_CLOSED, String STATUS_OPEN247, String STATUS_OPEN24,
                           String STATUS_OPEN_UNTIL, String STATUS_OPEN_AT, String STATUS_UNKNOWN) {
        this.restaurantsList = restaurantsList;
        this.workmatesList = workmatesList;
        this.KEY = KEY;
        this.STATUS_OPEN = STATUS_OPEN;
        this.STATUS_CLOSED = STATUS_CLOSED;
        this.STATUS_OPEN247 = STATUS_OPEN247;
        this.STATUS_OPEN24 = STATUS_OPEN24;
        this.STATUS_OPEN_UNTIL = STATUS_OPEN_UNTIL;
        this.STATUS_OPEN_AT = STATUS_OPEN_AT;
        this.STATUS_UNKNOWN = STATUS_UNKNOWN;
    }


    @NonNull
    @Override
    public ListViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create view holder and inflate its layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.restaurant_list_item, parent, false);

        return new ListViewViewHolder(view);
    }


    // Update view holder with restaurants
    @Override
    public void onBindViewHolder(@NonNull ListViewViewHolder viewHolder, int position) {
        viewHolder.updateWithRestaurants(this.restaurantsList.get(position), this.workmatesList, this.KEY,
                this.STATUS_OPEN, this.STATUS_CLOSED, this.STATUS_OPEN247, this.STATUS_OPEN24,
                this.STATUS_OPEN_UNTIL, this.STATUS_OPEN_AT, this.STATUS_UNKNOWN);
    }


    // Return the total count of restaurants
    @Override
    public int getItemCount() {
        return this.restaurantsList.size();
    }

}
