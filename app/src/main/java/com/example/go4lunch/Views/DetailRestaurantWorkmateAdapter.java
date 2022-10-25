package com.example.go4lunch.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;

import java.util.List;

public class DetailRestaurantWorkmateAdapter extends RecyclerView.Adapter<DetailRestaurantWorkmateViewHolder> {

    // Constructor
    public DetailRestaurantWorkmateAdapter() {

    }

    @NonNull
    @Override
    public DetailRestaurantWorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create view holder and inflate its layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.detail_restaurant_workmate_list_item, parent, false);

        return new DetailRestaurantWorkmateViewHolder(view);
    }

    // Update view holder with workmates
    @Override
    public void onBindViewHolder(@NonNull DetailRestaurantWorkmateViewHolder viewHolder, int position) {
        viewHolder.updateWithWorkmate();

    }

    // Return the total count of workmates who have selected this restaurant
    @Override
    public int getItemCount() {
        return 9;
    }


}
