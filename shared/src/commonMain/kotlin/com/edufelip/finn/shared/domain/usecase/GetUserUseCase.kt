package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.model.User
import com.edufelip.finn.shared.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserUseCase(private val repo: UserRepository) {
    operator fun invoke(id: String): Flow<User> = repo.getUser(id)
}
