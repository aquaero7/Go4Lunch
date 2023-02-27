package com.example.go4lunch.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapter extends FragmentStateAdapter {


    public PagerAdapter(@NonNull FragmentManager mgr, @NonNull Lifecycle lifecycle) {
        super(mgr, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Page to return
        switch (position){
            case 0: // Page number 1
                return MapViewFragment.newInstance();
            case 1: // Page number 2
                return ListViewFragment.newInstance();
            case 2: // Page number 3
                return WorkmatesFragment.newInstance();
            // default:    //Page number 1
            //     return MapViewFragment.newInstance();
        }
        return MapViewFragment.newInstance();   // For default = Page number 1
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
