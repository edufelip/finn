package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.dto.LikeRequestDto
import com.edufelip.finn.shared.data.remote.dto.PostDto
import com.edufelip.finn.shared.data.remote.dto.UserReferenceDto

class IosPostRemoteDataSource(
    private val api: IosBackendApi,
) : PostRemoteDataSource {
    override suspend fun getFeed(userId: String, page: Int): List<PostDto> =
        api.getFeed(userId, page)

    override suspend fun getCommunityPosts(communityId: Int, page: Int): List<PostDto> =
        api.getCommunityPosts(communityId, page)

    override suspend fun getUserPosts(userId: String, page: Int): List<PostDto> =
        api.getUserPosts(userId, page)

    override suspend fun createPost(
        content: String,
        userId: String,
        image: ByteArray?,
        communityId: Int?,
    ): PostDto =
        api.createPost(
            PostPayload(
                content = content,
                user_id = userId,
                community_id = communityId,
                image = image,
            ),
        )

    override suspend fun likePost(postId: Int, userId: String) {
        api.likePost(LikeRequestDto(postId = postId, userId = userId))
    }

    override suspend fun dislikePost(postId: Int, userId: String) {
        api.dislikePost(postId, UserReferenceDto(userId))
    }

    override suspend fun deletePost(postId: Int) {
        api.deletePost(postId)
    }
}
