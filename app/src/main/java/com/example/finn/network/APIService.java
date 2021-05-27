package com.example.finn.network;

import com.example.finn.data.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService {
    @GET("users")
    Call<List<User>> getUsersList();
}
