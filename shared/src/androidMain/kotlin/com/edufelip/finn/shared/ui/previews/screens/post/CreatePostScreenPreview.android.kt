package com.edufelip.finn.shared.ui.previews.screens.post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.screens.post.CreatePostScreen
import kotlinx.coroutines.flow.flowOf

private class PreviewPostRepository : PostRepository {
    override fun feed(userId: String, page: Int) = flowOf(emptyList<com.edufelip.finn.shared.domain.model.Post>())

    override fun postsByCommunity(communityId: Int, page: Int) = flowOf(emptyList<com.edufelip.finn.shared.domain.model.Post>())

    override fun like(postId: Int, userId: String) = flowOf(Unit)

    override fun dislike(postId: Int, userId: String) = flowOf(Unit)

    override fun postsByUser(userId: String, page: Int) = flowOf(emptyList<com.edufelip.finn.shared.domain.model.Post>())

    override fun createPost(content: String, userId: String, image: ByteArray?, communityId: Int?) = flowOf(
        com.edufelip.finn.shared.domain.model.Post(
            id = 1,
            content = content,
            communityId = communityId,
            communityTitle = null,
            communityImage = null,
            userId = userId,
            userName = "Preview",
            image = null,
            likesCount = 0,
            commentsCount = 0,
            isLiked = false,
        ),
    )

    override fun delete(postId: Int, userId: String, communityId: Int?) = flowOf(Unit)
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CreatePostScreenPreview() {
    ProvideStrings {
        val repo = remember { PreviewPostRepository() }
        CreatePostScreen(
            repo = repo,
            userIdProvider = { "preview-user" },
            pickImage = { ByteArray(0) },
            onCreated = {},
            onCancel = {},
            modifier = Modifier,
        )
    }
}
