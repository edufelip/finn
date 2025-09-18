package com.edufelip.finn.shared.domain.repository

import com.edufelip.finn.shared.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun feed(userId: String, page: Int): Flow<List<Post>>
    fun postsByCommunity(communityId: Int, page: Int): Flow<List<Post>>
    fun like(postId: Int, userId: String): Flow<Unit>
    fun dislike(postId: Int, userId: String): Flow<Unit>
    fun postsByUser(userId: String, page: Int): Flow<List<Post>>
    fun createPost(content: String, userId: String, image: ByteArray? = null, communityId: Int? = null): Flow<Post>
    fun delete(postId: Int, userId: String, communityId: Int? = null): Flow<Unit>
}
