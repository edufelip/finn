package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.model.Comment
import com.edufelip.finn.shared.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow

class AddCommentUseCase(private val repo: CommentRepository) {
    operator fun invoke(postId: Int, userId: String, content: String): Flow<Comment> =
        repo.add(postId, userId, content)
}
