package com.edufelip.finn.ui.models

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
)
