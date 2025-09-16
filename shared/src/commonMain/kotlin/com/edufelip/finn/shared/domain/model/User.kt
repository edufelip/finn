package com.edufelip.finn.shared.domain.model

data class User(
    val id: String,
    val name: String?,
    val photoUrl: String?,
    val joinedAtMillis: Long?,
)
