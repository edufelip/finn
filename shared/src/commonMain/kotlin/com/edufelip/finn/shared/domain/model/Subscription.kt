package com.edufelip.finn.shared.domain.model

data class Subscription(
    val id: Int,
    val userId: String?,
    val communityId: Int,
    val isModerator: Boolean = false,
)
