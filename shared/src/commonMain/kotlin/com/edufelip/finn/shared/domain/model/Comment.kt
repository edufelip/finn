package com.edufelip.finn.shared.domain.model

data class Comment(
    val id: Int,
    val postId: Int,
    val userName: String?,
    val content: String,
    val dateMillis: Long?,
)
