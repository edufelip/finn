package com.edufelip.finn.shared.domain.model

data class Post(
    val id: Int,
    val content: String,
    val communityId: Int?,
    val communityTitle: String?,
    val communityImage: String?,
    val userId: String?,
    val userName: String?,
    val image: String?,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean,
    val dateMillis: Long? = null,
    val cachedAtMillis: Long? = null,
)
