package com.projects.finn.data.network;

import com.projects.finn.models.Comment;
import com.projects.finn.models.Community;
import com.projects.finn.models.Post;
import com.projects.finn.models.User;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/users/{id}")
    Flowable<User> getUser(@Path("id") String id);

    @POST("/users")
    Flowable<User> createUser(@Body User user);

    @GET("/posts/users/{id}/feed")
    Flowable<List<Post>> getUserFeed(@Path("id") String id, @Query("page") int page);

    @GET("/posts/communities/{id}")
    Flowable<List<Post>> getPostsFromCommunity(@Path("id") int id, @Query("page") int page);

    @GET("/posts/{id}/likes")
    Flowable<Integer> getPostLikes(@Path("id") int id);

    @GET("/comments/posts/{id}")
    Flowable<List<Comment>> getCommentsPost(@Path("id") int id);

    @GET("/communities/{id}")
    Flowable<Community> getCommunity(@Path("id") int id);

    @GET("/communities/{id}/subscribers")
    Flowable<Integer> getCommunitySubscribersCount(@Path("id") int id);

    @Multipart
    @POST("/communities")
    Flowable<Community> saveCommunity(@Part("community")RequestBody requestBody, @Part MultipartBody.Part image);
}
