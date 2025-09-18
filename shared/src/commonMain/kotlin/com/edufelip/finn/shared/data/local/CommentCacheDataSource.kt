package com.edufelip.finn.shared.data.local

import com.edufelip.finn.shared.cache.Comment_cache
import com.edufelip.finn.shared.cache.FinnDatabase
import com.edufelip.finn.shared.domain.model.Comment

interface CommentCacheDataSource {
    suspend fun write(postId: Int, comments: List<Comment>)
    suspend fun read(postId: Int, maxAgeMillis: Long? = null): List<Comment>
    suspend fun clear(postId: Int)
}

class SqlDelightCommentCacheDataSource(
    private val database: FinnDatabase,
    private val timeProvider: () -> Long = { System.currentTimeMillis() },
) : CommentCacheDataSource {

    private val queries get() = database.cacheQueries

    override suspend fun write(postId: Int, comments: List<Comment>) {
        val scopeKey = scope(postId)
        database.transaction {
            queries.deleteCommentsByScope(scopeKey)
            val updatedAt = timeProvider()
            comments.forEach { comment ->
                queries.insertComment(
                    cache_key = cacheKey(scopeKey, comment.id),
                    scope = scopeKey,
                    comment_id = comment.id.toLong(),
                    post_id = comment.postId.toLong(),
                    user_id = comment.userId,
                    user_image = comment.userImage,
                    user_name = comment.userName,
                    content = comment.content,
                    date_millis = comment.dateMillis,
                    updated_at_millis = updatedAt,
                )
            }
        }
    }

    override suspend fun read(postId: Int, maxAgeMillis: Long?): List<Comment> {
        val scopeKey = scope(postId)
        val rows = queries.selectCommentsByScope(scopeKey).executeAsList()
        if (rows.isEmpty()) return emptyList()
        if (maxAgeMillis != null) {
            val newest = rows.maxOf { it.updated_at_millis }
            if (timeProvider() - newest > maxAgeMillis) {
                clear(postId)
                return emptyList()
            }
        }
        return rows.map { it.toDomain() }
    }

    override suspend fun clear(postId: Int) {
        queries.deleteCommentsByScope(scope(postId))
    }

    private fun scope(postId: Int) = "post:$postId"

    private fun cacheKey(scope: String, commentId: Int) = "$scope-$commentId"
}

private fun Comment_cache.toDomain(): Comment =
    Comment(
        id = comment_id.toInt(),
        postId = post_id.toInt(),
        userId = user_id,
        userImage = user_image,
        userName = user_name,
        content = content,
        dateMillis = date_millis,
        cachedAtMillis = updated_at_millis,
    )
