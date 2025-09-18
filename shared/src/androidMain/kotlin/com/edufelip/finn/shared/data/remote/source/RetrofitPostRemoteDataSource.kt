package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.api.ApiServiceV2
import com.edufelip.finn.shared.data.remote.dto.LikeRequestDto
import com.edufelip.finn.shared.data.remote.dto.PostDto
import com.edufelip.finn.shared.data.remote.dto.UserReferenceDto
import java.io.File
import java.util.UUID
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
class RetrofitPostRemoteDataSource(
    private val api: ApiServiceV2,
) : PostRemoteDataSource {

    override suspend fun getFeed(userId: String, page: Int): List<PostDto> =
        api.getUserFeed(userId, page)

    override suspend fun getCommunityPosts(communityId: Int, page: Int): List<PostDto> =
        api.getPostsFromCommunity(communityId, page)

    override suspend fun getUserPosts(userId: String, page: Int): List<PostDto> =
        api.getPostsFromUser(userId, page)

    override suspend fun createPost(
        content: String,
        userId: String,
        image: ByteArray?,
        communityId: Int?,
    ): PostDto {
        val payload = buildJsonObject {
            put("content", content)
            put("user_id", userId)
            communityId?.let { put("community_id", it) }
        }
        val body: RequestBody = payload.toString().toRequestBody("application/json".toMediaType())
        val imagePart = image?.let { bytes ->
            val temp = File.createTempFile("upload_${'$'}{UUID.randomUUID()}", ".jpg")
            temp.writeBytes(bytes)
            val requestBody = temp.asRequestBody("image/jpeg".toMediaType())
            MultipartBody.Part.createFormData("image", temp.name, requestBody)
        }
        return api.savePost(body, imagePart)
    }

    override suspend fun likePost(postId: Int, userId: String) {
        api.likePost(LikeRequestDto(postId = postId, userId = userId))
    }

    override suspend fun dislikePost(postId: Int, userId: String) {
        api.dislikePost(postId, UserReferenceDto(id = userId))
    }

    override suspend fun deletePost(postId: Int) {
        api.deletePost(postId)
    }
}
