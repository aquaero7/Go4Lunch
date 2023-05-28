package com.example.go4lunch.utilsforviews;

import android.util.SparseArray;
import android.view.View;

import com.example.go4lunch.R;

/**
 * Enumeration called by fragments and activities
 * to manage clicks on buttons regardless of native or local language.
 * Buttons are identified by their ID and not by they TAG
 * in order to make these references dynamic according to the device language
 * and thus avoid 'Constant expression required' error with switch/case method.
 */

public enum EventButtonClick {
    NONE(-1),
    FAB_SELECT(R.id.fab_selection),
    BTN_CALL(R.id.button_call),
    BTN_LIKE(R.id.button_like),
    BTN_WEBSITE(R.id.button_website),
    BTN_SAVE(R.id.button_save),
    SW_NOTIFICATION(R.id.switch_notification);

    private static class sc {
        static SparseArray<EventButtonClick> buttons = new SparseArray<>();
    }
    // Constructor
    EventButtonClick(int id) {
        sc.buttons.put(id, this);
    }

    public static EventButtonClick from(View view) {
        return sc.buttons.get(view.getId(), NONE);
    }


}
