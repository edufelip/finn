package com.edufelip.finn.shared.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LikeRequestDto(
    @SerialName("post_id")
    val postId: Int,
    @SerialName("user_id")
    val userId: String,
)
