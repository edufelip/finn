package com.edufelip.finn.shared.data.fake

import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.repository.PostRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PostRepositoryFake : PostRepository {
    override fun feed(userId: String, page: Int): Flow<List<Post>> = flow {
        delay(300)
        val base = (0 until 10).map { i ->
            Post(
                id = (page - 1) * 10 + i,
                content = "Sample post #${'$'}{(page - 1) * 10 + i}",
                communityTitle = "General",
                userName = "User ${'$'}i",
                image = null,
                likesCount = i * page,
                commentsCount = i,
                isLiked = false,
            )
        }
        emit(base)
    }

    override fun postsByCommunity(communityId: Int, page: Int): Flow<List<Post>> = feed("", page)

    override fun like(postId: Int, userId: String): Flow<Unit> = flow {
        delay(150)
        emit(Unit)
    }

    override fun dislike(postId: Int, userId: String): Flow<Unit> = flow {
        delay(150)
        emit(Unit)
    }

    override fun postsByUser(userId: String, page: Int): Flow<List<Post>> = feed(userId, page)

    override fun createPost(
        content: String,
        userId: String,
        image: ByteArray?,
        communityId: Int?,
    ): Flow<Post> = flow {
        delay(200)
        emit(
            Post(
                id = (0..100000).random(),
                content = content,
                communityTitle = communityId?.let { "Community ${'$'}it" } ?: "General",
                userName = "User",
                image = null,
                likesCount = 0,
                commentsCount = 0,
                isLiked = false,
            ),
        )
    }
}
