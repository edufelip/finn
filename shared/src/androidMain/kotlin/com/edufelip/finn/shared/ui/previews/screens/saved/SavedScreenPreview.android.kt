package com.edufelip.finn.shared.ui.previews.screens.saved

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.screens.saved.SavedScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

private class PreviewSavedPostRepository : PostRepository {
    private val samplePost = Post(
        id = 1,
        content = "Saved post preview content",
        communityId = null,
        communityTitle = "Compose",
        communityImage = null,
        userId = "preview",
        userName = "Preview User",
        image = null,
        likesCount = 0,
        commentsCount = 0,
        isLiked = false,
        cachedAtMillis = 1_699_756_800_000,
    )

    override fun feed(userId: String, page: Int): Flow<List<Post>> = flowOf(listOf(samplePost))
    override fun postsByCommunity(communityId: Int, page: Int): Flow<List<Post>> = flowOf(emptyList())
    override fun like(postId: Int, userId: String) = flowOf(Unit)
    override fun dislike(postId: Int, userId: String) = flowOf(Unit)
    override fun postsByUser(userId: String, page: Int): Flow<List<Post>> = flowOf(listOf(samplePost))
    override fun createPost(content: String, userId: String, image: ByteArray?, communityId: Int?) = flowOf(samplePost.copy(content = content))
    override fun delete(postId: Int, userId: String, communityId: Int?) = flowOf(Unit)
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SavedScreenPreview() {
    ProvideStrings {
        SavedScreen(
            userIdFlow = flowOf("preview"),
            repo = PreviewSavedPostRepository(),
            onBack = {},
            modifier = Modifier,
        )
    }
}
