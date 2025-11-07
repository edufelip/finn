package com.edufelip.finn.shared.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.edufelip.finn.shared.data.local.room.FinnCacheDatabase
import com.edufelip.finn.shared.domain.model.Comment
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
class RoomCommentCacheDataSourceTest {
    private lateinit var database: FinnCacheDatabase
    private lateinit var cache: RoomCommentCacheDataSource
    private val dispatcher = StandardTestDispatcher()
    private var now: Long = 0L

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, FinnCacheDatabase::class.java)
            .setQueryCoroutineContext(dispatcher)
            .allowMainThreadQueries()
            .build()
        cache = RoomCommentCacheDataSource(database) { now }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun writeReplacesScope() = runTest(dispatcher) {
        val postId = 10
        cache.write(postId, listOf(comment(1, postId, "First"), comment(2, postId, "Second")))
        var comments = cache.read(postId)
        assertEquals(2, comments.size)

        cache.write(postId, listOf(comment(3, postId, "Newest")))
        comments = cache.read(postId)
        assertEquals(listOf("Newest"), comments.map { it.content })
    }

    @Test
    fun staleEntriesAreCleared() = runTest(dispatcher) {
        val postId = 55
        now = 1000
        cache.write(postId, listOf(comment(1, postId, "old")))
        now = 2000

        val result = cache.read(postId, maxAgeMillis = 500)
        assertTrue(result.isEmpty())
        assertTrue(cache.read(postId).isEmpty())
    }

    private fun comment(id: Int, postId: Int, content: String) =
        Comment(
            id = id,
            postId = postId,
            userId = "u$id",
            userImage = null,
            userName = "User$id",
            content = content,
            dateMillis = null,
            cachedAtMillis = null,
        )
}
