package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.dto.UserDto

class IosUserRemoteDataSource : UserRemoteDataSource {
    override suspend fun getUser(id: String): UserDto =
        UserDto(id = id, name = "iOS User", photoUrl = null, joinedAtMillis = null)
}
