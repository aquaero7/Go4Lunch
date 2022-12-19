package com.example.go4lunch.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.WorkmateListItemBinding;

public class WorkmateViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView textView;

    public WorkmateViewHolder(@NonNull View itemView) {
        super(itemView);
        WorkmateListItemBinding binding = WorkmateListItemBinding.bind(itemView);
        imageView = binding.workmateItemPicture;
        textView = binding.workmateItemTitle;

        /*  // TODO : to be deleted cause replaced with ViewBinding
        imageView = itemView.findViewById(R.id.workmate_item_picture);
        textView = itemView.findViewById(R.id.workmate_item_title);
        */
    }

    public void updateWithWorkmate() {

        // Display workmate picture
        imageView.setImageResource(R.drawable.ic_baseline_account_circle_white_24);

        // Display workmate name
        String mText = "Workmate" + " is eating " + "specialties" + " (Restaurant)";
        textView.setText(mText);

    }

}
