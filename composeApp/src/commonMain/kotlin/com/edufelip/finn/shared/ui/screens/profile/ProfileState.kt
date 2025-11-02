package com.edufelip.finn.shared.ui.screens.profile

import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.model.User

data class ProfileState(
    val loading: Boolean = false,
    val user: User? = null,
    val posts: List<Post> = emptyList(),
    val nextPage: Int = 1,
    val error: String? = null,
)
