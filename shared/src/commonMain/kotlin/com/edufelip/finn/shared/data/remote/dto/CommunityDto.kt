package com.edufelip.finn.shared.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommunityDto(
    val id: Int,
    val title: String,
    val description: String? = null,
    val image: String? = null,
    @SerialName("user_id")
    val ownerId: String? = null,
    @SerialName("date")
    val createdAtMillis: Long? = null,
)
