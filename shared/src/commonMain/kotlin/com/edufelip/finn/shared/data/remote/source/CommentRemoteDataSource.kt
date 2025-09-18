package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.dto.CommentDto

interface CommentRemoteDataSource {
    suspend fun getComments(postId: Int, page: Int, limit: Int): List<CommentDto>
    suspend fun addComment(postId: Int, userId: String, content: String): CommentDto
}
