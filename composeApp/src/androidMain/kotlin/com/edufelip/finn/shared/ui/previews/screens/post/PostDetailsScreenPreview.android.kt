package com.edufelip.finn.shared.ui.previews.screens.post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.domain.model.Comment
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.repository.CommentRepository
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.domain.usecase.AddCommentUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommentsForPostUseCase
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.screens.post.PostDetailsScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

private class PreviewCommentRepository : CommentRepository {
    private val sampleComment = Comment(
        id = 1,
        postId = 1,
        userId = "preview-user",
        userImage = null,
        userName = "Preview Commenter",
        content = "Thanks for sharing!",
        dateMillis = 1_699_756_800_000,
        cachedAtMillis = 1_699_756_800_000,
    )

    override fun list(postId: Int, page: Int): Flow<List<Comment>> = flowOf(listOf(sampleComment))

    override fun add(postId: Int, userId: String, content: String): Flow<Comment> = flowOf(sampleComment.copy(content = content))
}

private class PreviewPostRepo : PostRepository {
    override fun feed(userId: String, page: Int) = flowOf(emptyList<Post>())
    override fun postsByCommunity(communityId: Int, page: Int) = flowOf(emptyList<Post>())
    override fun like(postId: Int, userId: String) = flowOf(Unit)
    override fun dislike(postId: Int, userId: String) = flowOf(Unit)
    override fun postsByUser(userId: String, page: Int) = flowOf(emptyList<Post>())
    override fun createPost(content: String, userId: String, image: ByteArray?, communityId: Int?) = flowOf(samplePost(userId = userId, content = content))
    override fun delete(postId: Int, userId: String, communityId: Int?) = flowOf(Unit)

    private fun samplePost(userId: String, content: String) = Post(
        id = 1,
        content = content,
        communityId = 5,
        communityTitle = "Compose",
        communityImage = null,
        userId = userId,
        userName = "Preview Author",
        image = null,
        likesCount = 12,
        commentsCount = 3,
        isLiked = false,
        dateMillis = 1_699_756_800_000,
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun PostDetailsScreenPreview() {
    ProvideStrings {
        val commentRepo = remember { PreviewCommentRepository() }
        val postRepo = remember { PreviewPostRepo() }
        val post = Post(
            id = 1,
            content = "Compose Multiplatform reaches parity with Android UI!",
            communityId = 5,
            communityTitle = "Compose",
            communityImage = null,
            userId = "preview-user",
            userName = "Preview Author",
            image = null,
            likesCount = 24,
            commentsCount = 3,
            isLiked = true,
            dateMillis = 1_699_756_800_000,
        )
        PostDetailsScreen(
            postId = post.id,
            post = post,
            postRepository = postRepo,
            currentUserId = "preview-user",
            onBack = {},
            onShare = {},
            onPostUpdated = {},
            onPostDeleted = {},
            getComments = GetCommentsForPostUseCase(commentRepo),
            addComment = AddCommentUseCase(commentRepo),
            userIdProvider = { "preview-user" },
            modifier = Modifier,
        )
    }
}
