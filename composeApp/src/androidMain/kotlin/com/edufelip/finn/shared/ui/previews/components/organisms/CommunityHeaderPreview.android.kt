package com.edufelip.finn.shared.ui.previews.components.organisms

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.domain.model.Subscription
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.components.organisms.CommunityHeader

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CommunityHeaderPreview() {
    ProvideStrings {
        CommunityHeader(
            community = Community(
                id = 1,
                title = "Compose Fans",
                description = "All about Compose multiplatform",
                image = null,
                subscribersCount = 1024,
                ownerId = "owner",
                createdAtMillis = 1_699_756_800_000,
            ),
            currentUserId = "owner",
            membership = Subscription(id = 1, userId = "owner", communityId = 1),
            membershipLoading = false,
            deleting = false,
            onBack = {},
            onSubscribe = {},
            onUnsubscribe = {},
            onDelete = {},
        )
    }
}
