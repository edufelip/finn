package com.edufelip.finn.shared.data.local

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.edufelip.finn.shared.cache.FinnDatabase
import com.edufelip.finn.shared.domain.model.Community
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SqlDelightCommunityCacheDataSourceTest {
    private lateinit var driver: JdbcSqliteDriver
    private lateinit var database: FinnDatabase
    private lateinit var cache: SqlDelightCommunityCacheDataSource
    private var now: Long = 0L

    @BeforeTest
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        FinnDatabase.Schema.create(driver)
        database = FinnDatabase(driver)
        now = System.currentTimeMillis()
        cache = SqlDelightCommunityCacheDataSource(database) { now }
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun readSearchHonoursExpiry() {
        val communities = listOf(community(1), community(2))
        cache.writeSearch("kotlin", communities)

        val fresh = cache.readSearch("kotlin", maxAgeMillis = 1_000L)
        assertEquals(2, fresh.size)

        now += 2_001L
        val stale = cache.readSearch("kotlin", maxAgeMillis = 1_000L)
        assertTrue(stale.isEmpty())
    }

    @Test
    fun getByIdHonoursExpiry() {
        val community = community(5)
        cache.writeDetails(community)
        val fresh = cache.getById(5, maxAgeMillis = 1_000L)
        assertEquals(community.id, fresh?.id)

        now += 2_001L
        val stale = cache.getById(5, maxAgeMillis = 1_000L)
        assertNull(stale)
    }

    private fun community(id: Int) =
        Community(
            id = id,
            title = "Community $id",
            description = "desc",
            image = null,
            subscribersCount = 123,
            ownerId = null,
            createdAtMillis = null,
        )
}
