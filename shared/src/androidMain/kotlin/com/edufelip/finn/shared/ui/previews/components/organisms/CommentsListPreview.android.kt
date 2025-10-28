package com.edufelip.finn.shared.ui.previews.components.organisms

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.domain.model.Comment
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.components.organisms.CommentsList

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CommentsListPreview() {
    ProvideStrings {
        val sample = Comment(
            id = 1,
            content = "Great insight on Compose MPP!",
            dateMillis = 1_699_756_800_000,
            userId = "user-1",
            userImage = null,
            userName = "Previewer",
            postId = 42,
            cachedAtMillis = 1_699_756_800_000,
        )
        CommentsList(
            comments = listOf(sample),
            onSend = {},
            onReply = {},
            endReached = true,
            onLoadMore = {},
            cacheAgeMillis = sample.cachedAtMillis,
            errorMessage = null,
        )
    }
}
