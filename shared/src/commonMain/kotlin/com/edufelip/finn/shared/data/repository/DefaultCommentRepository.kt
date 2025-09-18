package com.edufelip.finn.shared.data.repository

import com.edufelip.finn.shared.data.CacheTtlProvider
import com.edufelip.finn.shared.data.local.CommentCacheDataSource
import com.edufelip.finn.shared.data.mappers.toDomain
import com.edufelip.finn.shared.data.mappers.toDomainComments
import com.edufelip.finn.shared.data.remote.source.CommentRemoteDataSource
import com.edufelip.finn.shared.domain.model.Comment
import com.edufelip.finn.shared.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DefaultCommentRepository(
    private val remote: CommentRemoteDataSource,
    private val cache: CommentCacheDataSource,
    private val ttlProvider: CacheTtlProvider,
    private val pageSize: Int,
) : CommentRepository {

    override fun list(postId: Int, page: Int): Flow<List<Comment>> = flow {
        if (page == 1) {
            val cached = cache.read(postId, maxAgeMillis = ttlProvider.commentCacheTtlMillis)
            if (cached.isNotEmpty()) emit(cached)
        }

        val list = remote.getComments(postId, page, pageSize).toDomainComments()
        if (page == 1) cache.write(postId, list)
        emit(list)
    }

    override fun add(postId: Int, userId: String, content: String): Flow<Comment> = flow {
        val saved = remote.addComment(postId, userId, content)
        cache.clear(postId)
        emit(saved.toDomain())
    }
}
