package com.edufelip.finn.shared.presentation.auth

data class AuthState(
    val loading: Boolean = false,
    val userId: String? = null,
    val error: String? = null,
)
