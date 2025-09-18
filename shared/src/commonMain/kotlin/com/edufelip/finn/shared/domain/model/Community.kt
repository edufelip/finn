package com.edufelip.finn.shared.domain.model

data class Community(
    val id: Int,
    val title: String,
    val description: String?,
    val image: String?,
    val subscribersCount: Int,
    val ownerId: String? = null,
    val createdAtMillis: Long? = null,
    val cachedAtMillis: Long? = null,
)
