package com.edufelip.finn.sharedimpl

import com.edufelip.finn.data.network.ApiServiceV2
import com.edufelip.finn.shared.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Named
import com.edufelip.finn.domain.models.Comment as ApiComment
import com.edufelip.finn.shared.domain.model.Comment as SharedComment

class CommentRepositoryAndroid @Inject constructor(
    private val api: ApiServiceV2,
    @Named("commentPageSize") private val pageSize: Int,
) : CommentRepository {
    override fun list(postId: Int, page: Int): Flow<List<SharedComment>> = flow {
        val list = api.getCommentsPost(postId, page, pageSize).map { c ->
            SharedComment(
                id = c.id,
                postId = c.postId,
                userName = c.userName,
                content = c.content ?: "",
                dateMillis = c.date?.time,
            )
        }
        emit(list)
    }

    override fun add(postId: Int, userId: String, content: String): Flow<SharedComment> = flow {
        val c = ApiComment()
        c.postId = postId
        c.userId = userId
        c.content = content
        val saved = api.saveComment(c)
        emit(
            SharedComment(
                id = saved.id,
                postId = saved.postId,
                userName = saved.userName,
                content = saved.content ?: "",
                dateMillis = saved.date?.time,
            ),
        )
    }
}
