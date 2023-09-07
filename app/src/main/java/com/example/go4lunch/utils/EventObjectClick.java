package com.example.go4lunch.utils;

import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;

import com.example.go4lunch.R;

/**
 * Enumeration called by fragments and activities
 * to manage clicks on buttons or menu items regardless of native or local language.
 * Buttons or menu items are identified by their ID and not by they TAG
 * in order to make these references dynamic according to the device language
 * and thus avoid 'Constant expression required' error with switch/case method.
 */

public enum EventObjectClick {
    // Enum for all objects
    NONE(-1),

    // Enum for views
    FAB_SELECT(R.id.fab_selection),
    BTN_CALL(R.id.button_call),
    BTN_LIKE(R.id.button_like),
    BTN_WEBSITE(R.id.button_website),
    BTN_SAVE(R.id.button_save),
    SW_NOTIFICATION(R.id.switch_notification),

    // Enum for menu items
    MENU_ITEM_SEARCH(R.id.menu_activity_main_search),
    MENU_ITEM_LUNCH(R.id.activity_main_drawer_lunch),
    MENU_ITEM_SETTINGS(R.id.activity_main_drawer_settings),
    MENU_ITEM_LOGOUT(R.id.activity_main_drawer_logout),
    MENU_ITEM_DELETE_ACCOUNT(R.id.activity_main_drawer_delete_account);


    private static class sc {
        static SparseArray<EventObjectClick> objects = new SparseArray<>();
    }

    // Constructor
    EventObjectClick(int id) {
        sc.objects.put(id, this);
    }

    public static EventObjectClick fromView(View view) {
        return sc.objects.get(view.getId(), NONE);
    }

    public static EventObjectClick fromMenuItem(MenuItem item) {
        return sc.objects.get(item.getItemId(), NONE);
    }

}
