package com.edufelip.finn.shared.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.edufelip.finn.shared.data.local.room.FinnCacheDatabase
import com.edufelip.finn.shared.domain.model.Post
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class RoomPostCacheDataSourceTest {
    private lateinit var database: FinnCacheDatabase
    private lateinit var cache: RoomPostCacheDataSource
    private var now: Long = 0L
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, FinnCacheDatabase::class.java)
            .setQueryCoroutineContext(dispatcher)
            .allowMainThreadQueries()
            .build()
        now = 0L
        cache = RoomPostCacheDataSource(database) { now }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun pagingReplacesFirstPageAndPreservesOrdering() = runTest(dispatcher) {
        val scope = PostCacheScope.Feed("user")
        cache.write(scope, page = 1, pageSize = 3, posts = (1..3).map { post(it, "#$it") })
        var cached = cache.read(scope)
        assertEquals(listOf("#1", "#2", "#3"), cached.map { it.content })

        cache.write(scope, page = 2, pageSize = 3, posts = listOf(post(4, "#4")))
        cached = cache.read(scope)
        assertEquals(listOf("#1", "#2", "#3", "#4"), cached.map { it.content })
    }

    @Test
    fun evictsOverflowEntries() = runTest(dispatcher) {
        val scope = PostCacheScope.Feed("overflow")
        (0 until 120).chunked(10).forEachIndexed { page, chunk ->
            cache.write(scope, page = page + 1, pageSize = 10, posts = chunk.map { post(it, "$it") })
        }

        val cached = cache.read(scope)
        assertEquals(100, cached.size)
        assertEquals("20", cached.first().content)
        assertEquals("119", cached.last().content)
    }

    @Test
    fun staleEntriesAreCleared() = runTest(dispatcher) {
        val scope = PostCacheScope.Feed("stale")
        now = 1_000L
        cache.write(scope, page = 1, pageSize = 1, posts = listOf(post(1, "fresh")))

        now = 1_000L + STALE_THRESHOLD + 1
        val cached = cache.read(scope, maxAgeMillis = STALE_THRESHOLD)
        assertTrue(cached.isEmpty())
        assertTrue(cache.read(scope).isEmpty())
    }

    private fun post(id: Int, content: String) =
        Post(
            id = id,
            content = content,
            communityId = null,
            communityTitle = null,
            communityImage = null,
            userId = null,
            userName = null,
            image = null,
            likesCount = 0,
            commentsCount = 0,
            isLiked = false,
            cachedAtMillis = null,
        )
}

private const val STALE_THRESHOLD = 500L
