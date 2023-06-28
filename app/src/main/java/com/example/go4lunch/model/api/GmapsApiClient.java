package com.example.go4lunch.model.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GmapsApiClient {

    private static Retrofit retrofit /* = null */;

    public static GmapsApiInterface getApiClient() {

        // Build retrofit
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(GmapsApiInterface.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        // Create object for the interface
        GmapsApiInterface apiClient = retrofit.create(GmapsApiInterface.class);

        // Return the API interface object
        return apiClient;
    }

}
