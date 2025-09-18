package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.repository.CommunityRepository
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.asResult
import kotlinx.coroutines.flow.Flow

class UnsubscribeFromCommunityUseCase(private val repo: CommunityRepository) {
    operator fun invoke(userId: String, communityId: Int): Flow<Result<Unit>> =
        repo.unsubscribe(userId, communityId).asResult()
}
