package com.projects.finn.data.network;

import com.projects.finn.data.models.User;
import com.projects.finn.repositories.IUserRepository;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("/users/{id}")
    Call<User> getUser(@Path("id") String id);

    @POST("/users")
    Call<User> createUser(@Body User user);
}
