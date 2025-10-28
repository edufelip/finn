package com.edufelip.finn.shared.ui.screens.auth

data class AuthState(
    val loading: Boolean = false,
    val userId: String? = null,
    val error: String? = null,
)
