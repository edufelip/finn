package com.edufelip.finn.shared.ui.previews.components.organisms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.components.organisms.PostCard

@Preview(showBackground = true)
@Composable
private fun PostCardPreview() {
    ProvideStrings {
        PostCard(
            post = Post(
                id = 1,
                content = "Previewing a post card built with Compose!",
                communityId = 10,
                communityTitle = "Previewers",
                communityImage = null,
                userId = "user",
                userName = "Preview User",
                image = null,
                likesCount = 12,
                commentsCount = 3,
                isLiked = false,
                dateMillis = 1_699_756_800_000,
                cachedAtMillis = null,
            ),
            modifier = Modifier,
            onClick = {},
            onCommunityClick = {},
            onToggleLike = {},
            onCommentsClick = {},
            onShareClick = {},
            onHide = {},
        )
    }
}
