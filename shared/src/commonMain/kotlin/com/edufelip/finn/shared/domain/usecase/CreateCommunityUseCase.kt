package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.asResult
import kotlinx.coroutines.flow.Flow

class CreateCommunityUseCase(private val repo: CommunityRepository) {
    operator fun invoke(title: String, description: String?, image: ByteArray? = null): Flow<Result<Community>> =
        repo.create(title, description, image).asResult()
}
