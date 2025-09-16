package com.edufelip.finn.shared.presentation.post

data class CreatePostState(
    val content: String = "",
    val imageBytes: ByteArray? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val createdId: Int? = null,
)

