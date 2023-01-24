package com.example.go4lunch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;

import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewViewHolder> {

    private final String KEY;
    private final String STATUS_OPEN;
    private final String STATUS_CLOSED;
    private final String STATUS_OPEN247;
    private final String STATUS_OPEN_UNTIL;
    private final String STATUS_OPEN_AT;
    private List<Restaurant> restaurantsList;

    // Constructor
    public ListViewAdapter(List<Restaurant> restaurantsList, String KEY, String STATUS_OPEN, String STATUS_CLOSED,
                           String STATUS_OPEN247, String STATUS_OPEN_UNTIL, String STATUS_OPEN_AT) {
        this.restaurantsList = restaurantsList;
        this.KEY = KEY;
        this.STATUS_OPEN = STATUS_OPEN;
        this.STATUS_CLOSED = STATUS_CLOSED;
        this.STATUS_OPEN247 = STATUS_OPEN247;
        this.STATUS_OPEN_UNTIL = STATUS_OPEN_UNTIL;
        this.STATUS_OPEN_AT = STATUS_OPEN_AT;
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
        viewHolder.updateWithRestaurants(this.restaurantsList.get(position), this.KEY, this.STATUS_OPEN,
                this.STATUS_CLOSED, this.STATUS_OPEN247, this.STATUS_OPEN_UNTIL, this.STATUS_OPEN_AT);
    }

    // Return the total count of restaurants
    @Override
    public int getItemCount() {
        return this.restaurantsList.size();
    }

}
