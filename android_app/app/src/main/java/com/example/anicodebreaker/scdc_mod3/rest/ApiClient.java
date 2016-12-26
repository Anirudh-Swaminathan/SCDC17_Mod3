package com.example.anicodebreaker.scdc_mod3.rest;

import android.content.Context;
import android.content.SharedPreferences;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by anicodebreaker on 26/12/16.
 */

public class ApiClient {

    // Base URL to be used by the App for all api calls
    // This is a dummy value, which will be changed on starting the app
    public static String BASE_URL = "http://omdbapi.com";
    private static Retrofit retrofit = null;

    /**
     * function to create an instance of the Interface
     * @return retrofit object
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * no args constructor required for the use of changeApiBaseUrl function
     */
    private ApiClient(){

    }

    /**
     * change the BASE_URL of the app
     * @param newApiBaseUrl the url to set the base_url to
     */
    public static void changeApiBaseUrl(String newApiBaseUrl) {
        BASE_URL = newApiBaseUrl;

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
