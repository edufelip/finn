package com.edufelip.finn.shared.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: Int,
    val content: String? = null,
    @SerialName("date")
    val dateMillis: Long? = null,
    val image: String? = null,
    @SerialName("community_title")
    val communityTitle: String? = null,
    @SerialName("community_image")
    val communityImage: String? = null,
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("user_name")
    val userName: String? = null,
    @SerialName("community_id")
    val communityId: Int? = null,
    @SerialName("likes_count")
    val likesCount: Int = 0,
    @SerialName("comments_count")
    val commentsCount: Int = 0,
    @SerialName("is_liked")
    val isLiked: Boolean = false,
    val comments: List<CommentDto>? = null,
)
