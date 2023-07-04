package com.example.go4lunch;

import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {

    private static MainApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MainApplication getInstance() {
        if (instance == null) {
            throw new IllegalStateException("This method should only be called after onCreate()");
        }
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

}
