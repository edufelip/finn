package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.model.Subscription
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.asResult
import kotlinx.coroutines.flow.Flow

class GetCommunitySubscriptionUseCase(private val repo: CommunityRepository) {
    operator fun invoke(userId: String, communityId: Int): Flow<Result<Subscription?>> =
        repo.getSubscription(userId, communityId).asResult()
}
