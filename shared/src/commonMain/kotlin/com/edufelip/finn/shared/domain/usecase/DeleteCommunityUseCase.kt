package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.repository.CommunityRepository
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.asResult
import kotlinx.coroutines.flow.Flow

class DeleteCommunityUseCase(private val repo: CommunityRepository) {
    operator fun invoke(id: Int): Flow<Result<Unit>> = repo.delete(id).asResult()
}
