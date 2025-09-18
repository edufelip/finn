package com.edufelip.finn.shared.data.local

import com.edufelip.finn.shared.cache.FinnDatabase
import com.edufelip.finn.shared.cache.Post_cache
import com.edufelip.finn.shared.domain.model.Post

interface PostCacheDataSource {
    suspend fun write(scope: PostCacheScope, page: Int, pageSize: Int, posts: List<Post>)
    suspend fun read(scope: PostCacheScope, limit: Int? = null, maxAgeMillis: Long? = null): List<Post>
    suspend fun clear(scope: PostCacheScope)
}

class SqlDelightPostCacheDataSource(
    private val database: FinnDatabase,
    private val timeProvider: () -> Long = { System.currentTimeMillis() },
) : PostCacheDataSource {

    private val queries get() = database.cacheQueries

    override suspend fun write(scope: PostCacheScope, page: Int, pageSize: Int, posts: List<Post>) {
        val scopeKey = scope.key
        if (posts.isEmpty()) return
        database.transaction {
            if (page == 1) {
                queries.deletePostsByScope(scopeKey)
            }

            val baseIndex = maxOf((page - 1) * pageSize, 0)
            val updatedAt = timeProvider()
            posts.forEachIndexed { index, post ->
                queries.insertPost(
                    cache_key = cacheKey(scopeKey, post.id),
                    scope = scopeKey,
                    post_id = post.id.toLong(),
                    content = post.content,
                    community_id = post.communityId?.toLong(),
                    community_title = post.communityTitle,
                    community_image = post.communityImage,
                    user_id = post.userId,
                    user_name = post.userName,
                    image = post.image,
                    likes_count = post.likesCount.toLong(),
                    comments_count = post.commentsCount.toLong(),
                    is_liked = if (post.isLiked) 1L else 0L,
                    date_millis = post.dateMillis,
                    ordering = (baseIndex + index).toLong(),
                    updated_at_millis = updatedAt,
                )
            }

            val total = queries.countPostsByScope(scopeKey).executeAsOne().toInt()
            if (total > MAX_ITEMS_PER_SCOPE) {
                val overflow = total - MAX_ITEMS_PER_SCOPE
                queries.selectPostKeysByScope(scopeKey, overflow.toLong())
                    .executeAsList()
                    .forEach { key -> queries.deletePostByKey(key) }
            }
        }
    }

    override suspend fun read(scope: PostCacheScope, limit: Int?, maxAgeMillis: Long?): List<Post> {
        val scopeKey = scope.key
        val rows = queries.selectPostsByScope(scopeKey).executeAsList()
        if (rows.isEmpty()) return emptyList()
        if (maxAgeMillis != null) {
            val newest = rows.maxOf { it.updated_at_millis }
            if (timeProvider() - newest > maxAgeMillis) {
                clear(scope)
                return emptyList()
            }
        }
        val limited = if (limit != null) rows.take(limit) else rows
        return limited.map { it.toDomain() }
    }

    override suspend fun clear(scope: PostCacheScope) {
        queries.deletePostsByScope(scope.key)
    }

    private fun cacheKey(scopeKey: String, postId: Int) = "$scopeKey-$postId"

    companion object {
        private const val MAX_ITEMS_PER_SCOPE = 100
    }
}

private fun Post_cache.toDomain(): Post =
    Post(
        id = post_id.toInt(),
        content = content,
        communityId = community_id?.toInt(),
        communityTitle = community_title,
        communityImage = community_image,
        userId = user_id,
        userName = user_name,
        image = image,
        likesCount = likes_count.toInt(),
        commentsCount = comments_count.toInt(),
        isLiked = is_liked != 0L,
        dateMillis = date_millis,
        cachedAtMillis = updated_at_millis,
    )
