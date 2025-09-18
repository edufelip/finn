package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.api.ApiServiceV2
import com.edufelip.finn.shared.data.remote.dto.CommentCreateRequestDto
import com.edufelip.finn.shared.data.remote.dto.CommentDto
class RetrofitCommentRemoteDataSource(
    private val api: ApiServiceV2,
) : CommentRemoteDataSource {
    override suspend fun getComments(postId: Int, page: Int, limit: Int): List<CommentDto> =
        api.getCommentsPost(postId, page, limit)

    override suspend fun addComment(postId: Int, userId: String, content: String): CommentDto {
        val request = CommentCreateRequestDto(
            postId = postId,
            userId = userId,
            content = content,
        )
        return api.saveComment(request)
    }
}
