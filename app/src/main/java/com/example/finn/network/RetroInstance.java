package com.example.finn.network;

import com.example.finn.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroInstance {
    public static String BASE_URL = BuildConfig.BACKEND_IP;

    private static Retrofit retrofit;

    public static Retrofit getRetroClient(){
        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://" + BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
