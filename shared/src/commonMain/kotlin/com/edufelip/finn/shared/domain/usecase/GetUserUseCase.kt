package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.model.User
import com.edufelip.finn.shared.domain.repository.UserRepository
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.asResult
import kotlinx.coroutines.flow.Flow

class GetUserUseCase(private val repo: UserRepository) {
    operator fun invoke(id: String): Flow<Result<User>> = repo.getUser(id).asResult()
}
