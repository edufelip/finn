package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.dto.CommentCreateRequestDto
import com.edufelip.finn.shared.data.remote.dto.CommentDto

class IosCommentRemoteDataSource(
    private val api: IosBackendApi,
) : CommentRemoteDataSource {
    override suspend fun getComments(postId: Int, page: Int, limit: Int): List<CommentDto> =
        api.getComments(postId, page, limit)

    override suspend fun addComment(postId: Int, userId: String, content: String): CommentDto =
        api.addComment(
            CommentCreateRequestDto(
                postId = postId,
                userId = userId,
                content = content,
            ),
        )
}
