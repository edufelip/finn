package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.dto.UserDto

interface UserRemoteDataSource {
    suspend fun getUser(id: String): UserDto
}
