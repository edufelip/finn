package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow

class SearchCommunitiesUseCase(private val repo: CommunityRepository) {
    operator fun invoke(query: String): Flow<List<Community>> = repo.search(query)
}
