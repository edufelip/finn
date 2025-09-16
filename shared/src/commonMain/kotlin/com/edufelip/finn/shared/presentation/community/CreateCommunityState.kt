package com.edufelip.finn.shared.presentation.community

data class CreateCommunityState(
    val title: String = "",
    val description: String = "",
    val image: ByteArray? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val createdId: Int? = null,
)

