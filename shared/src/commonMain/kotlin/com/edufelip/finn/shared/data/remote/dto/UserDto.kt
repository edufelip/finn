package com.edufelip.finn.shared.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String? = null,
    val name: String? = null,
    @SerialName("photo")
    val photoUrl: String? = null,
    @SerialName("date")
    val joinedAtMillis: Long? = null,
)
