package com.edufelip.finn.shared.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.edufelip.finn.shared.data.local.room.FinnCacheDatabase
import com.edufelip.finn.shared.domain.model.Community
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class RoomCommunityCacheDataSourceTest {
    private lateinit var database: FinnCacheDatabase
    private lateinit var cache: RoomCommunityCacheDataSource
    private val dispatcher = StandardTestDispatcher()
    private var now: Long = 0L

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, FinnCacheDatabase::class.java)
            .setQueryCoroutineContext(dispatcher)
            .allowMainThreadQueries()
            .build()
        cache = RoomCommunityCacheDataSource(database) { now }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun cachesSearchResultsWithOrder() = runTest(dispatcher) {
        val list = listOf(community(1, "One"), community(2, "Two"))
        cache.writeSearch("Compose", list)

        val cached = cache.readSearch("Compose")
        assertEquals(list.map { it.title }, cached.map { it.title })
    }

    @Test
    fun respectsTtlForSearch() = runTest(dispatcher) {
        val list = listOf(community(1, "Stale"))
        now = 1000
        cache.writeSearch("stale", list)

        now = 2000
        val cached = cache.readSearch("stale", maxAgeMillis = 500)
        assertTrue(cached.isEmpty())
    }

    @Test
    fun getByIdHonorsTtl() = runTest(dispatcher) {
        now = 1000
        val item = community(42, "Answer")
        cache.writeDetails(item)

        now = 2000
        assertNull(cache.getById(42, maxAgeMillis = 500))
    }

    private fun community(id: Int, title: String) =
        Community(
            id = id,
            title = title,
            description = null,
            image = null,
            subscribersCount = 0,
            ownerId = null,
            createdAtMillis = null,
            cachedAtMillis = null,
        )
}
