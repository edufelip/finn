package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.model.Comment
import com.edufelip.finn.shared.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow

class GetCommentsForPostUseCase(private val repo: CommentRepository) {
    operator fun invoke(postId: Int, page: Int): Flow<List<Comment>> = repo.list(postId, page)
}
