package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.dto.PostDto

interface PostRemoteDataSource {
    suspend fun getFeed(userId: String, page: Int): List<PostDto>
    suspend fun getCommunityPosts(communityId: Int, page: Int): List<PostDto>
    suspend fun getUserPosts(userId: String, page: Int): List<PostDto>
    suspend fun createPost(
        content: String,
        userId: String,
        image: ByteArray?,
        communityId: Int?,
    ): PostDto
    suspend fun likePost(postId: Int, userId: String)
    suspend fun dislikePost(postId: Int, userId: String)
    suspend fun deletePost(postId: Int)
}
