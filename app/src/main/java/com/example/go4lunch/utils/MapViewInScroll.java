package com.example.go4lunch.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

/**
 * Layout to make MapView properly scrollable inside a ViewPager2. Provided as a solution to the problem
 * where pages of ViewPager2 have nested scrollable elements that scroll in the same direction as
 * ViewPager2.
 * (This solution is different from NestedScrollableHost solution used to to wrap a scrollable component
 * inside a ViewPager2).
 */
public class MapViewInScroll extends MapView {

    public MapViewInScroll(Context context) {
        super(context);
    }

    public MapViewInScroll(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MapViewInScroll(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public MapViewInScroll(Context context, GoogleMapOptions googleMapOptions) {
        super(context, googleMapOptions);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

}
