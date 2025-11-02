package com.edufelip.finn.shared.ui.screens.saved

import com.edufelip.finn.shared.domain.model.Post

data class SavedState(
    val loading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val nextPage: Int = 1,
    val error: String? = null,
)
