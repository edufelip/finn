package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow

class CreateCommunityUseCase(private val repo: CommunityRepository) {
    operator fun invoke(title: String, description: String?, image: ByteArray? = null): Flow<Community> =
        repo.create(title, description, image)
}
