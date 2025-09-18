package com.edufelip.finn.shared.data.local

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.edufelip.finn.shared.cache.FinnDatabase
import com.edufelip.finn.shared.domain.model.Comment
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SqlDelightCommentCacheDataSourceTest {
    private lateinit var driver: JdbcSqliteDriver
    private lateinit var database: FinnDatabase
    private lateinit var cache: SqlDelightCommentCacheDataSource
    private var now: Long = 0L

    @BeforeTest
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        FinnDatabase.Schema.create(driver)
        database = FinnDatabase(driver)
        now = System.currentTimeMillis()
        cache = SqlDelightCommentCacheDataSource(database) { now }
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun readHonoursExpiry() {
        cache.write(1, listOf(comment(1), comment(2)))
        val fresh = cache.read(1, maxAgeMillis = 1_000L)
        assertEquals(2, fresh.size)

        now += 2_001L
        val stale = cache.read(1, maxAgeMillis = 1_000L)
        assertTrue(stale.isEmpty())
    }

    private fun comment(id: Int) = Comment(
        id = id,
        postId = 1,
        userId = "user$id",
        userImage = null,
        userName = "U$id",
        content = "content$id",
        dateMillis = null,
    )
}
