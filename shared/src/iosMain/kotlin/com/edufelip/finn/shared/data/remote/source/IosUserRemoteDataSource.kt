package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.dto.UserDto

class IosUserRemoteDataSource(
    private val api: IosBackendApi,
) : UserRemoteDataSource {
    override suspend fun getUser(id: String): UserDto =
        api.getUser(id)
}
