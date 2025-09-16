package com.edufelip.finn.data.network

import com.edufelip.finn.domain.models.Comment
import com.edufelip.finn.domain.models.Community
import com.edufelip.finn.domain.models.Like
import com.edufelip.finn.domain.models.Post
import com.edufelip.finn.domain.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceV2 {
    @GET("/posts/users/{id}/feed")
    suspend fun getUserFeed(
        @Path("id") id: String,
        @Query("page") page: Int,
    ): List<Post>

    @GET("/communities")
    suspend fun getCommunities(
        @Query("search") search: String,
    ): List<Community>

    @GET("/communities/{id}")
    suspend fun getCommunity(@Path("id") id: Int): Community

    @GET("/communities/{id}/subscribers")
    suspend fun getCommunitySubscribersCount(@Path("id") id: Int): Int

    @GET("/posts/communities/{id}")
    suspend fun getPostsFromCommunity(
        @Path("id") id: Int,
        @Query("page") page: Int,
    ): List<Post>

    @GET("/posts/users/{id}")
    suspend fun getPostsFromUser(
        @Path("id") id: String,
        @Query("page") page: Int,
    ): List<Post>

    @GET("/users/{id}")
    suspend fun getUser(@Path("id") id: String): User

    @GET("/posts/{id}/likes")
    suspend fun getPostLikes(@Path("id") id: Int): Int

    @POST("/posts/likes")
    suspend fun likePost(@Body like: Like): Like

    @POST("/posts/likes/{id}")
    suspend fun dislikePost(@Path("id") postId: Int, @Body user: User)

    @Multipart
    @POST("/communities")
    suspend fun saveCommunity(
        @Part("community") requestBody: RequestBody,
        @Part image: MultipartBody.Part?,
    ): Community

    @Multipart
    @POST("/posts")
    suspend fun savePost(
        @Part("post") requestBody: RequestBody,
        @Part image: MultipartBody.Part?,
    ): Post

    @POST("/devices/tokens")
    suspend fun uploadFcmToken(@Body body: Map<String, String>)

    @GET("/comments/posts/{id}")
    suspend fun getCommentsPost(
        @Path("id") id: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): List<Comment>

    @POST("/comments")
    suspend fun saveComment(@Body comment: Comment): Comment
}
