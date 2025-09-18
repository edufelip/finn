package com.edufelip.finn.shared.data.repository

import com.edufelip.finn.shared.data.mappers.toDomain
import com.edufelip.finn.shared.data.remote.source.UserRemoteDataSource
import com.edufelip.finn.shared.domain.model.User
import com.edufelip.finn.shared.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DefaultUserRepository(
    private val remote: UserRemoteDataSource,
) : UserRepository {
    override fun getUser(id: String): Flow<User> = flow {
        emit(remote.getUser(id).toDomain())
    }
}
