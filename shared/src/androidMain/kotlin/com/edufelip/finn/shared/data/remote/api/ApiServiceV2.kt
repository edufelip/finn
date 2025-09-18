package com.edufelip.finn.shared.data.remote.api

import com.edufelip.finn.shared.data.remote.dto.CommentCreateRequestDto
import com.edufelip.finn.shared.data.remote.dto.CommentDto
import com.edufelip.finn.shared.data.remote.dto.CommunityDto
import com.edufelip.finn.shared.data.remote.dto.LikeRequestDto
import com.edufelip.finn.shared.data.remote.dto.PostDto
import com.edufelip.finn.shared.data.remote.dto.SubscriptionDto
import com.edufelip.finn.shared.data.remote.dto.UserDto
import com.edufelip.finn.shared.data.remote.dto.UserReferenceDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
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
    ): List<PostDto>

    @GET("/communities")
    suspend fun getCommunities(
        @Query("search") search: String,
    ): List<CommunityDto>

    @GET("/communities/{id}")
    suspend fun getCommunity(@Path("id") id: Int): CommunityDto

    @GET("/communities/{id}/subscribers")
    suspend fun getCommunitySubscribersCount(@Path("id") id: Int): Int

    @POST("/communities/subscribe")
    suspend fun subscribeToCommunity(@Body subscription: SubscriptionDto): SubscriptionDto

    @POST("/communities/unsubscribe")
    suspend fun unsubscribeFromCommunity(@Body subscription: SubscriptionDto)

    @GET("/communities/{comm_id}/users/{user_id}")
    suspend fun getSubscription(
        @Path("user_id") userId: String,
        @Path("comm_id") communityId: Int,
    ): SubscriptionDto?

    @DELETE("/communities/{id}")
    suspend fun deleteCommunity(@Path("id") id: Int)

    @GET("/posts/communities/{id}")
    suspend fun getPostsFromCommunity(
        @Path("id") id: Int,
        @Query("page") page: Int,
    ): List<PostDto>

    @GET("/posts/users/{id}")
    suspend fun getPostsFromUser(
        @Path("id") id: String,
        @Query("page") page: Int,
    ): List<PostDto>

    @GET("/users/{id}")
    suspend fun getUser(@Path("id") id: String): UserDto

    @GET("/posts/{id}/likes")
    suspend fun getPostLikes(@Path("id") id: Int): Int

    @POST("/posts/likes")
    suspend fun likePost(@Body like: LikeRequestDto)

    @POST("/posts/likes/{id}")
    suspend fun dislikePost(
        @Path("id") postId: Int,
        @Body user: UserReferenceDto,
    )

    @Multipart
    @POST("/communities")
    suspend fun saveCommunity(
        @Part("community") requestBody: RequestBody,
        @Part image: MultipartBody.Part?,
    ): CommunityDto

    @Multipart
    @POST("/posts")
    suspend fun savePost(
        @Part("post") requestBody: RequestBody,
        @Part image: MultipartBody.Part?,
    ): PostDto

    @DELETE("/posts/{id}")
    suspend fun deletePost(@Path("id") id: Int)

    @POST("/devices/tokens")
    suspend fun uploadFcmToken(@Body body: Map<String, String>)

    @GET("/comments/posts/{id}")
    suspend fun getCommentsPost(
        @Path("id") id: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): List<CommentDto>

    @POST("/comments")
    suspend fun saveComment(@Body comment: CommentCreateRequestDto): CommentDto
}
