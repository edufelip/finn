package com.edufelip.finn.shared.data.repository

import com.edufelip.finn.shared.data.CacheTtlProvider
import com.edufelip.finn.shared.data.local.PostCacheDataSource
import com.edufelip.finn.shared.data.local.PostCacheScope
import com.edufelip.finn.shared.data.mappers.toDomain
import com.edufelip.finn.shared.data.mappers.toDomainPosts
import com.edufelip.finn.shared.data.remote.source.PostRemoteDataSource
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DefaultPostRepository(
    private val remote: PostRemoteDataSource,
    private val cache: PostCacheDataSource,
    private val ttlProvider: CacheTtlProvider,
    private val pageSize: Int = DEFAULT_PAGE_SIZE,
) : PostRepository {

    override fun feed(userId: String, page: Int): Flow<List<Post>> = flow {
        val scope = PostCacheScope.Feed(userId)
        val ttl = ttlProvider.feedCacheTtlMillis
        if (page == 1) {
            val cached = cache.read(scope, maxAgeMillis = ttl)
            if (cached.isNotEmpty()) emit(cached)
        }

        val items = remote.getFeed(userId, page).toDomainPosts()
        cache.write(scope, page, pageSize, items)
        emit(items)
    }

    override fun postsByCommunity(communityId: Int, page: Int): Flow<List<Post>> = flow {
        val scope = PostCacheScope.Community(communityId)
        val ttl = ttlProvider.communityDetailsTtlMillis
        if (page == 1) {
            val cached = cache.read(scope, maxAgeMillis = ttl)
            if (cached.isNotEmpty()) emit(cached)
        }

        val items = remote.getCommunityPosts(communityId, page).toDomainPosts()
        cache.write(scope, page, pageSize, items)
        emit(items)
    }

    override fun like(postId: Int, userId: String): Flow<Unit> = flow {
        remote.likePost(postId, userId)
        cache.clear(PostCacheScope.Feed(userId))
        cache.clear(PostCacheScope.User(userId))
        emit(Unit)
    }

    override fun dislike(postId: Int, userId: String): Flow<Unit> = flow {
        remote.dislikePost(postId, userId)
        cache.clear(PostCacheScope.Feed(userId))
        cache.clear(PostCacheScope.User(userId))
        emit(Unit)
    }

    override fun postsByUser(userId: String, page: Int): Flow<List<Post>> = flow {
        val scope = PostCacheScope.User(userId)
        val ttl = ttlProvider.feedCacheTtlMillis
        if (page == 1) {
            val cached = cache.read(scope, maxAgeMillis = ttl)
            if (cached.isNotEmpty()) emit(cached)
        }

        val items = remote.getUserPosts(userId, page).toDomainPosts()
        cache.write(scope, page, pageSize, items)
        emit(items)
    }

    override fun createPost(
        content: String,
        userId: String,
        image: ByteArray?,
        communityId: Int?,
    ): Flow<Post> = flow {
        val dto = remote.createPost(content, userId, image, communityId)
        cache.clear(PostCacheScope.User(userId))
        cache.clear(PostCacheScope.Feed(userId))
        emit(dto.toDomain().copy(content = dto.content ?: content))
    }

    override fun delete(postId: Int, userId: String, communityId: Int?): Flow<Unit> = flow {
        remote.deletePost(postId)
        cache.clear(PostCacheScope.Feed(userId))
        cache.clear(PostCacheScope.User(userId))
        communityId?.let { cache.clear(PostCacheScope.Community(it)) }
        emit(Unit)
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 10
    }
}
