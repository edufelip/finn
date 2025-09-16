package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

class GetCommunityPostsUseCase(private val repo: PostRepository) {
    operator fun invoke(communityId: Int, page: Int): Flow<List<Post>> = repo.postsByCommunity(communityId, page)
}
