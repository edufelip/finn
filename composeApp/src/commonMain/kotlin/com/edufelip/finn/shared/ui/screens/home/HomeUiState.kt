package com.edufelip.finn.shared.ui.screens.home

import com.edufelip.finn.shared.domain.model.Post

data class HomeUiState(
    val loading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null,
    val nextPage: Int = 1,
    val endReached: Boolean = false,
)
