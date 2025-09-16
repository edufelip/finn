package com.edufelip.finn.shared.domain.repository

import com.edufelip.finn.shared.domain.model.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    fun list(postId: Int, page: Int): Flow<List<Comment>>
    fun add(postId: Int, userId: String, content: String): Flow<Comment>
}
