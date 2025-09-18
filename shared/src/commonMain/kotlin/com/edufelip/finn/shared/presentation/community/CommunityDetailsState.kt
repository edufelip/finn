package com.edufelip.finn.shared.presentation.community

import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.domain.model.Subscription

data class CommunityDetailsState(
    val loading: Boolean = false,
    val community: Community? = null,
    val posts: List<com.edufelip.finn.shared.domain.model.Post> = emptyList(),
    val loadingMore: Boolean = false,
    val nextPage: Int = 1,
    val endReached: Boolean = false,
    val error: String? = null,
    val membership: Subscription? = null,
    val membershipLoading: Boolean = false,
    val deleting: Boolean = false,
)
