package com.edufelip.finn.sharedimpl

import com.edufelip.finn.data.network.ApiServiceV2
import com.edufelip.finn.domain.models.Like
import com.edufelip.finn.domain.models.User
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import com.edufelip.finn.shared.domain.model.Post as SharedPost

class PostRepositoryAndroid @Inject constructor(
    private val api: ApiServiceV2,
) : PostRepository {
    override fun feed(userId: String, page: Int): Flow<List<SharedPost>> = flow {
        val items = api.getUserFeed(userId, page).map { p ->
            SharedPost(
                id = p.id,
                content = p.content ?: "",
                communityTitle = p.communityTitle,
                userName = p.userName,
                image = p.image,
                likesCount = p.likesCount,
                commentsCount = p.commentsCount,
                isLiked = p.isLiked,
                dateMillis = p.date?.time,
            )
        }
        emit(items)
    }

    override fun postsByCommunity(communityId: Int, page: Int): Flow<List<SharedPost>> = flow {
        val items = api.getPostsFromCommunity(communityId, page).map { p ->
            SharedPost(
                id = p.id,
                content = p.content ?: "",
                communityTitle = p.communityTitle,
                userName = p.userName,
                image = p.image,
                likesCount = p.likesCount,
                commentsCount = p.commentsCount,
                isLiked = p.isLiked,
                dateMillis = p.date?.time,
            )
        }
        emit(items)
    }

    override fun like(postId: Int, userId: String): Flow<Unit> = flow {
        val like = Like()
        like.postId = postId
        like.userId = userId
        api.likePost(like)
        emit(Unit)
    }

    override fun dislike(postId: Int, userId: String): Flow<Unit> = flow {
        val u = User()
        u.id = userId
        api.dislikePost(postId, u)
        emit(Unit)
    }

    override fun postsByUser(userId: String, page: Int): Flow<List<SharedPost>> = flow {
        val items = api.getPostsFromUser(userId, page).map { p ->
            SharedPost(
                id = p.id,
                content = p.content ?: "",
                communityTitle = p.communityTitle,
                userName = p.userName,
                image = p.image,
                likesCount = p.likesCount,
                commentsCount = p.commentsCount,
                isLiked = p.isLiked,
                dateMillis = p.date?.time,
            )
        }
        emit(items)
    }

    override fun createPost(
        content: String,
        userId: String,
        image: ByteArray?,
        communityId: Int?,
    ): Flow<SharedPost> = flow {
        val payload = mutableMapOf<String, Any>(
            "content" to content,
            "user_id" to userId,
        )
        if (communityId != null) payload["community_id"] = communityId
        val json = Gson().toJson(payload)
        val body: RequestBody = json.toRequestBody("application/json".toMediaType())
        val imagePart: MultipartBody.Part? = image?.let {
            val temp = File.createTempFile("upload_${'$'}{UUID.randomUUID()}", ".jpg")
            temp.writeBytes(it)
            val req = temp.asRequestBody("image/jpeg".toMediaType())
            MultipartBody.Part.createFormData("image", temp.name, req)
        }
        val p = api.savePost(body, imagePart)
        emit(
            SharedPost(
                id = p.id,
                content = p.content ?: content,
                communityTitle = p.communityTitle,
                userName = p.userName,
                image = p.image,
                likesCount = p.likesCount,
                commentsCount = p.commentsCount,
                isLiked = p.isLiked,
                dateMillis = p.date?.time,
            ),
        )
    }
}
