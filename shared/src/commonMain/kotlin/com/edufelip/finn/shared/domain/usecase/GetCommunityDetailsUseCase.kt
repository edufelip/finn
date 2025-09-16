package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow

class GetCommunityDetailsUseCase(private val repo: CommunityRepository) {
    operator fun invoke(id: Int): Flow<Community> = repo.getById(id)
}
