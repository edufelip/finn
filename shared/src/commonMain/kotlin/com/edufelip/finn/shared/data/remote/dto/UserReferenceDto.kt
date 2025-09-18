package com.edufelip.finn.shared.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserReferenceDto(
    @SerialName("id")
    val id: String,
)
