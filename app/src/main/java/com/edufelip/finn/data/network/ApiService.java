package com.edufelip.finn.data.network;

import com.edufelip.finn.domain.models.Comment;
import com.edufelip.finn.domain.models.Community;
import com.edufelip.finn.domain.models.Like;
import com.edufelip.finn.domain.models.Post;
import com.edufelip.finn.domain.models.Subscription;
import com.edufelip.finn.domain.models.User;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    //user
    @GET("/users/{id}")
    Flowable<User> getUser(@Path("id") String id);

    @POST("/users")
    Flowable<User> createUser(@Body User user);

    @DELETE("/users/{id}")
    Flowable<String> deleteUser(@Path("id") String userId);

    //posts
    @GET("/posts/users/{id}/feed")
    Flowable<List<Post>> getUserFeed(@Path("id") String id, @Query("page") int page);

    @GET("/posts/communities/{id}")
    Flowable<List<Post>> getPostsFromCommunity(@Path("id") int id, @Query("page") int page);

    @GET("/posts/users/{id}")
    Flowable<List<Post>> getPostsFromUser(@Path("id") String id, @Query("page") int page);

    @GET("/posts/{id}/likes")
    Flowable<Integer> getPostLikes(@Path("id") int id);

    @GET("/posts/{postId}/users/{userId}")
    Flowable<Integer> findLike(@Path("postId") int postId, @Path("userId") String userId);

    @POST("/posts/likes")
    Flowable<Like> likePost(@Body Like like);

    @POST("/posts/likes/{id}")
    Flowable<Void> dislikePost(@Path("id") int postId, @Body User user);

    @Multipart
    @POST("/posts")
    Flowable<Post> savePost(@Part("post")RequestBody requestBody, @Part MultipartBody.Part image);

    @DELETE("/posts/{id}")
    Flowable<Void> deletePost(@Path("id") int postId);

    //comments
    @GET("/comments/posts/{id}")
    Flowable<List<Comment>> getCommentsPost(@Path("id") int id);

    @POST("/comments")
    Flowable<Comment> saveComment(@Body Comment comment);

    //communities
    @GET("/communities")
    Flowable<List<Community>> getCommunities(@Query("search") String search);

    @GET("/communities/{id}")
    Flowable<Community> getCommunity(@Path("id") int id);

    @GET("/communities/users/{id}")
    Flowable<List<Community>> getCommunitiesFromUser(@Path("id") String userId);

    @GET("/communities/{id}/subscribers")
    Flowable<Integer> getCommunitySubscribersCount(@Path("id") int id);

    @Multipart
    @POST("/communities")
    Flowable<Community> saveCommunity(@Part("community")RequestBody requestBody, @Part MultipartBody.Part image);

    @POST("/communities/subscribe")
    Flowable<Subscription> subscribeToCommunity(@Body Subscription subscription);

    @POST("/communities/unsubscribe")
    Flowable<Void> unsubscribeFromCommunity(@Body Subscription subscription);

    @GET("/communities/{comm_id}/users/{user_id}")
    Flowable<Subscription> getSubscription(@Path("user_id") String userId, @Path("comm_id") int communityId);

    @DELETE("/communities/{id}")
    Flowable<Void> deleteCommunity(@Path("id") int communityId);
}
