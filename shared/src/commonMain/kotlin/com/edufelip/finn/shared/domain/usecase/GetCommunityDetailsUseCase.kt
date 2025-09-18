package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.asResult
import kotlinx.coroutines.flow.Flow

class GetCommunityDetailsUseCase(private val repo: CommunityRepository) {
    operator fun invoke(id: Int): Flow<Result<Community>> = repo.getById(id).asResult()
}
