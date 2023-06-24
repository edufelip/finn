package com.projects.finn.ui.delegators.auth

interface IAuthExecutor {
    val type: AuthTypes
    suspend fun signOut()
}