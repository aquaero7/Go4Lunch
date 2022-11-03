package com.example.go4lunch.Views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;

public class WorkmateViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView textView;

    public WorkmateViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.workmate_item_picture);
        textView = itemView.findViewById(R.id.workmate_item_title);
    }

    public void updateWithWorkmate() {

        // Display workmate picture
        imageView.setImageResource(R.drawable.ic_baseline_account_circle_white_24);

        // Display workmate name
        String mText = "Workmate" + " is eating " + "specialties" + " (Restaurant)";
        textView.setText(mText);

    }

}
