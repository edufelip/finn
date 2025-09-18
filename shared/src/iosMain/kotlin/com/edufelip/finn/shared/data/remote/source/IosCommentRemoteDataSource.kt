package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.dto.CommentDto

class IosCommentRemoteDataSource : CommentRemoteDataSource {
    override suspend fun getComments(postId: Int, page: Int, limit: Int): List<CommentDto> = sampleComments(postId)

    override suspend fun addComment(postId: Int, userId: String, content: String): CommentDto =
        CommentDto(id = (1..100_000).random(), postId = postId, userId = userId, userName = "iOS", content = content)

    private fun sampleComments(postId: Int): List<CommentDto> =
        (0 until 5).map { index ->
            CommentDto(
                id = index + 1,
                postId = postId,
                userId = "user-$index",
                userName = "Commenter ${index + 1}",
                content = "Insight #$index",
            )
        }
}
