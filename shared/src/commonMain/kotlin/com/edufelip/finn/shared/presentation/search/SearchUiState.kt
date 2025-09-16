package com.edufelip.finn.shared.presentation.search

import com.edufelip.finn.shared.domain.model.Community

data class SearchUiState(
    val query: String = "",
    val loading: Boolean = false,
    val results: List<Community> = emptyList(),
    val error: String? = null,
)
