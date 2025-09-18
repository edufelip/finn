package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.api.ApiServiceV2
import com.edufelip.finn.shared.data.remote.dto.UserDto
class RetrofitUserRemoteDataSource(
    private val api: ApiServiceV2,
) : UserRemoteDataSource {
    override suspend fun getUser(id: String): UserDto = api.getUser(id)
}
