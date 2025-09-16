package com.edufelip.finn.shared.domain.repository

import com.edufelip.finn.shared.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(id: String): Flow<User>
}
