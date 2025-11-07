package com.edufelip.finn.shared.data.local

import androidx.room.withTransaction
import com.edufelip.finn.shared.data.local.room.FinnCacheDatabase
import com.edufelip.finn.shared.data.local.room.PostCacheDao
import com.edufelip.finn.shared.data.local.room.PostCacheEntity
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.util.currentTimeMillis

interface PostCacheDataSource {
    suspend fun write(scope: PostCacheScope, page: Int, pageSize: Int, posts: List<Post>)
    suspend fun read(scope: PostCacheScope, limit: Int? = null, maxAgeMillis: Long? = null): List<Post>
    suspend fun clear(scope: PostCacheScope)
}

class RoomPostCacheDataSource(
    private val database: FinnCacheDatabase,
    private val timeProvider: () -> Long = { currentTimeMillis() },
) : PostCacheDataSource {

    private val dao: PostCacheDao
        get() = database.postCacheDao()

    override suspend fun write(scope: PostCacheScope, page: Int, pageSize: Int, posts: List<Post>) {
        val scopeKey = scope.key
        if (posts.isEmpty()) return
        database.withTransaction {
            if (page == 1) {
                dao.deleteByScope(scopeKey)
            }

            val baseIndex = maxOf((page - 1) * pageSize, 0)
            val updatedAt = timeProvider()
            dao.insertAll(
                posts.mapIndexed { index, post ->
                    post.toEntity(
                        scopeKey = scopeKey,
                        cacheKey = cacheKey(scopeKey, post.id),
                        ordering = (baseIndex + index).toLong(),
                        updatedAt = updatedAt,
                    )
                },
            )

            val total = dao.countByScope(scopeKey)
            if (total > MAX_ITEMS_PER_SCOPE) {
                val overflow = total - MAX_ITEMS_PER_SCOPE
                val keys = dao.selectKeysByScope(scopeKey, overflow)
                if (keys.isNotEmpty()) dao.deleteByCacheKeys(keys)
            }
        }
    }

    override suspend fun read(scope: PostCacheScope, limit: Int?, maxAgeMillis: Long?): List<Post> {
        val scopeKey = scope.key
        val rows = dao.selectByScope(scopeKey)
        if (rows.isEmpty()) return emptyList()
        if (maxAgeMillis != null) {
            val newest = rows.maxOf { it.updatedAtMillis }
            if (timeProvider() - newest > maxAgeMillis) {
                clear(scope)
                return emptyList()
            }
        }
        val limited = if (limit != null) rows.take(limit) else rows
        return limited.map { it.toDomain() }
    }

    override suspend fun clear(scope: PostCacheScope) {
        dao.deleteByScope(scope.key)
    }

    private fun cacheKey(scopeKey: String, postId: Int) = "$scopeKey-$postId"

    companion object {
        private const val MAX_ITEMS_PER_SCOPE = 100
    }
}

private fun PostCacheEntity.toDomain(): Post =
    Post(
        id = postId.toInt(),
        content = content,
        communityId = communityId?.toInt(),
        communityTitle = communityTitle,
        communityImage = communityImage,
        userId = userId,
        userName = userName,
        image = image,
        likesCount = likesCount.toInt(),
        commentsCount = commentsCount.toInt(),
        isLiked = isLiked,
        dateMillis = dateMillis,
        cachedAtMillis = updatedAtMillis,
    )

private fun Post.toEntity(
    scopeKey: String,
    cacheKey: String,
    ordering: Long,
    updatedAt: Long,
) = PostCacheEntity(
    cacheKey = cacheKey,
    scope = scopeKey,
    postId = id.toLong(),
    content = content,
    communityId = communityId?.toLong(),
    communityTitle = communityTitle,
    communityImage = communityImage,
    userId = userId,
    userName = userName,
    image = image,
    likesCount = likesCount.toLong(),
    commentsCount = commentsCount.toLong(),
    isLiked = isLiked,
    dateMillis = dateMillis,
    ordering = ordering,
    updatedAtMillis = updatedAt,
)
