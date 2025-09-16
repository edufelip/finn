package com.edufelip.finn.shared.domain.model

data class Post(
    val id: Int,
    val content: String,
    val communityTitle: String?,
    val userName: String?,
    val image: String?,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean,
    val dateMillis: Long? = null,
)
