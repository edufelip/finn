package com.edufelip.finn.shared.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: Int,
    val content: String? = null,
    @SerialName("date")
    val dateMillis: Long? = null,
    @SerialName("post_id")
    val postId: Int = 0,
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("user_image")
    val userImage: String? = null,
    @SerialName("user_name")
    val userName: String? = null,
)
