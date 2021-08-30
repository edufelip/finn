package com.projects.finn.data.network;

import com.projects.finn.data.models.User;
import com.projects.finn.repositories.IUserRepository;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("/users/{id}")
    Flowable<User> getUser(@Path("id") String id);

    @POST("/users")
    Flowable<User> createUser(@Body User user);
}
