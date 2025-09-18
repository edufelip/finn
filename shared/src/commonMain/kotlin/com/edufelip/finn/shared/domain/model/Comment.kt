package com.edufelip.finn.shared.domain.model

data class Comment(
    val id: Int,
    val postId: Int,
    val userId: String?,
    val userImage: String?,
    val userName: String?,
    val content: String,
    val dateMillis: Long?,
    val cachedAtMillis: Long? = null,
)
