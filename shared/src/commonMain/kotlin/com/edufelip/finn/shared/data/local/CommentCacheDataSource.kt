package com.edufelip.finn.shared.data.local

import androidx.room.withTransaction
import com.edufelip.finn.shared.data.local.room.CommentCacheDao
import com.edufelip.finn.shared.data.local.room.CommentCacheEntity
import com.edufelip.finn.shared.data.local.room.FinnCacheDatabase
import com.edufelip.finn.shared.domain.model.Comment
import com.edufelip.finn.shared.util.currentTimeMillis

interface CommentCacheDataSource {
    suspend fun write(postId: Int, comments: List<Comment>)
    suspend fun read(postId: Int, maxAgeMillis: Long? = null): List<Comment>
    suspend fun clear(postId: Int)
}

class RoomCommentCacheDataSource(
    private val database: FinnCacheDatabase,
    private val timeProvider: () -> Long = { currentTimeMillis() },
) : CommentCacheDataSource {

    private val dao: CommentCacheDao
        get() = database.commentCacheDao()

    override suspend fun write(postId: Int, comments: List<Comment>) {
        val scopeKey = scope(postId)
        database.withTransaction {
            dao.deleteByScope(scopeKey)
            val updatedAt = timeProvider()
            dao.insertAll(
                comments.map { comment ->
                    comment.toEntity(
                        scope = scopeKey,
                        cacheKey = cacheKey(scopeKey, comment.id),
                        updatedAt = updatedAt,
                    )
                },
            )
        }
    }

    override suspend fun read(postId: Int, maxAgeMillis: Long?): List<Comment> {
        val scopeKey = scope(postId)
        val rows = dao.selectByScope(scopeKey)
        if (rows.isEmpty()) return emptyList()
        if (maxAgeMillis != null) {
            val newest = rows.maxOf { it.updatedAtMillis }
            if (timeProvider() - newest > maxAgeMillis) {
                clear(postId)
                return emptyList()
            }
        }
        return rows.map { it.toDomain() }
    }

    override suspend fun clear(postId: Int) {
        dao.deleteByScope(scope(postId))
    }

    private fun scope(postId: Int) = "post:$postId"

    private fun cacheKey(scope: String, commentId: Int) = "$scope-$commentId"
}

private fun CommentCacheEntity.toDomain(): Comment =
    Comment(
        id = commentId.toInt(),
        postId = postId.toInt(),
        userId = userId,
        userImage = userImage,
        userName = userName,
        content = content,
        dateMillis = dateMillis,
        cachedAtMillis = updatedAtMillis,
    )

private fun Comment.toEntity(
    scope: String,
    cacheKey: String,
    updatedAt: Long,
) = CommentCacheEntity(
    cacheKey = cacheKey,
    scope = scope,
    commentId = id.toLong(),
    postId = postId.toLong(),
    userId = userId,
    userImage = userImage,
    userName = userName,
    content = content,
    dateMillis = dateMillis,
    updatedAtMillis = updatedAt,
)
