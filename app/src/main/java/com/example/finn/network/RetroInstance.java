package com.example.finn.network;

import com.example.finn.PrivateInfo;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroInstance {
    public static String BASE_URL = PrivateInfo.url;

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
