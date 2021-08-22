package com.projects.finn.data.network;

import com.projects.finn.data.models.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService {
    @GET("/posts")
    Call<List<Post>> getPostList();
}
