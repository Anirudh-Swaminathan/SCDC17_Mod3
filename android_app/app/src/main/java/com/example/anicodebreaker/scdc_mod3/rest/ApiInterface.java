package com.example.anicodebreaker.scdc_mod3.rest;

import com.example.anicodebreaker.scdc_mod3.model.HealthData;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by anicodebreaker on 26/12/16.
 */

public interface ApiInterface {
    @GET("/data")
    Call<HealthData> getLatestData();
}
