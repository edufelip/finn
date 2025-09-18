package com.edufelip.finn.shared.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionDto(
    val id: Int,
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("community_id")
    val communityId: Int = 0,
    @SerialName("is_moderator")
    val isModerator: Boolean = false,
)
