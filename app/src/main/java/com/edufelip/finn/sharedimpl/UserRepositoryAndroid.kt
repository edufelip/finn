package com.edufelip.finn.sharedimpl

import com.edufelip.finn.data.network.ApiServiceV2
import com.edufelip.finn.shared.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.edufelip.finn.shared.domain.model.User as SharedUser

class UserRepositoryAndroid @Inject constructor(
    private val api: ApiServiceV2,
) : UserRepository {
    override fun getUser(id: String): Flow<SharedUser> = flow {
        val u = api.getUser(id)
        emit(
            SharedUser(
                id = u.id ?: "",
                name = u.name,
                photoUrl = u.photo,
                joinedAtMillis = u.date?.time,
            ),
        )
    }
}
