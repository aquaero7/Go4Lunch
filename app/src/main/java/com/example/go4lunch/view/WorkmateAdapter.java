package com.example.go4lunch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;

public class WorkmateAdapter extends RecyclerView.Adapter<WorkmateViewHolder> {

    // Constructor
    public WorkmateAdapter() {

    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create view holder and inflate its layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.workmate_list_item, parent, false);

        return new WorkmateViewHolder(view);

    }

    // Update view holder with workmates
    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder viewHolder, int position) {
        viewHolder.updateWithWorkmate();

    }

    // Return the total count of workmates
    @Override
    public int getItemCount() {
        return 12;
    }
}
