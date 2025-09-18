package com.edufelip.finn.shared.data.local

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.edufelip.finn.shared.cache.FinnDatabase
import com.edufelip.finn.shared.domain.model.Post
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class SqlDelightPostCacheDataSourceTest {
    private lateinit var driver: JdbcSqliteDriver
    private lateinit var database: FinnDatabase
    private lateinit var cache: SqlDelightPostCacheDataSource

    private var now: Long = 0L

    @BeforeTest
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        FinnDatabase.Schema.create(driver)
        database = FinnDatabase(driver)
        now = System.currentTimeMillis()
        cache = SqlDelightPostCacheDataSource(database) { now }
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun writeAndRead_firstPageReplacesAndSorts() = runTest {
        val scope = PostCacheScope.Feed("user")
        val firstPage = (0 until 3).map { idx -> post(id = idx + 1, content = "P$idx") }
        cache.write(scope, page = 1, pageSize = 3, posts = firstPage)

        val cached = cache.read(scope)
        assertEquals(listOf("P0", "P1", "P2"), cached.map { it.content })

        val secondPage = listOf(post(id = 10, content = "P3"))
        cache.write(scope, page = 2, pageSize = 3, posts = secondPage)

        val merged = cache.read(scope)
        assertEquals(4, merged.size)
        assertEquals("P3", merged.last().content)
    }

    @Test
    fun eviction_keepsMostRecentEntries() = runTest {
        val scope = PostCacheScope.Feed("evict")
        val entries = (0 until 120).map { idx -> post(id = idx, content = "#$idx") }
        entries.chunked(10).forEachIndexed { page, chunk ->
            cache.write(scope, page = page + 1, pageSize = 10, posts = chunk)
        }

        val cached = cache.read(scope)
        assertEquals(100, cached.size)
        assertEquals("#20", cached.first().content)
        assertEquals("#119", cached.last().content)
    }

    @Test
    fun staleEntriesAreIgnoredAndCleared() = runTest {
        val scope = PostCacheScope.Feed("stale")
        now = 1_000L
        cache.write(scope, page = 1, pageSize = 1, posts = listOf(post(id = 1, content = "fresh")))

        now = 1_000L + STALE_THRESHOLD + 1
        val cached = cache.read(scope, maxAgeMillis = STALE_THRESHOLD)
        assertTrue(cached.isEmpty())

        val afterClear = cache.read(scope)
        assertTrue(afterClear.isEmpty())
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
