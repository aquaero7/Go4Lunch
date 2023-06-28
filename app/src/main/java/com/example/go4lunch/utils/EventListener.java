package com.example.go4lunch.utils;

/**
 * Interface implemented by MainActivity (managing the SearchView) and called by fragments
 * in order to toggle SearchView visibility from each fragment
 */

public interface EventListener {

    void toggleSearchViewVisibility();


}
