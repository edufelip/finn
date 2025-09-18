package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.dto.PostDto
import kotlin.random.Random

class IosPostRemoteDataSource : PostRemoteDataSource {
    override suspend fun getFeed(userId: String, page: Int): List<PostDto> = samplePosts(page)

    override suspend fun getCommunityPosts(communityId: Int, page: Int): List<PostDto> =
        samplePosts(page).map { it.copy(communityId = communityId, communityTitle = "Community $communityId") }

    override suspend fun getUserPosts(userId: String, page: Int): List<PostDto> = samplePosts(page)

    override suspend fun createPost(
        content: String,
        userId: String,
        image: ByteArray?,
        communityId: Int?,
    ): PostDto = PostDto(
        id = Random.nextInt(1, 100_000),
        content = content,
        communityId = communityId,
        communityTitle = communityId?.let { "Community $it" } ?: "General",
        userId = userId,
        userName = "iOS",
        likesCount = 0,
        commentsCount = 0,
        isLiked = false,
    )

    override suspend fun likePost(postId: Int, userId: String) {}

    override suspend fun dislikePost(postId: Int, userId: String) {}

    override suspend fun deletePost(postId: Int) {}

    private fun samplePosts(page: Int): List<PostDto> =
        (0 until 10).map { index ->
            val id = (page - 1) * 10 + index
            PostDto(
                id = id,
                content = "Sample post #$id",
                communityId = 1,
                communityTitle = "Multiplatform",
                userId = "user-$index",
                userName = "Author ${index + 1}",
                likesCount = index * page,
                commentsCount = index,
                isLiked = false,
            )
        }
}
