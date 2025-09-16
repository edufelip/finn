package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

class GetFeedUseCase(private val repo: PostRepository) {
    operator fun invoke(userId: String, page: Int): Flow<List<Post>> = repo.feed(userId, page)
}
