package com.edufelip.finn.shared.pagination

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface Paginator<Key, Item> {
    suspend fun loadNext()
    suspend fun reset()
}

class DefaultPaginator<Key, Item>(
    private val initialKey: Key,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (Key) -> List<Item>,
    private val getNextKey: (Key, List<Item>) -> Key,
    private val isEnd: (List<Item>) -> Boolean,
    private val onError: (Throwable) -> Unit,
    private val onSuccess: (items: List<Item>, newKey: Key, endReached: Boolean) -> Unit,
) : Paginator<Key, Item> {

    private val mutex = Mutex()
    private var currentKey: Key = initialKey
    private var isMakingRequest: Boolean = false
    private var endReached: Boolean = false

    override suspend fun loadNext() {
        mutex.withLock {
            if (isMakingRequest || endReached) return
            isMakingRequest = true
        }
        onLoadUpdated(true)
        try {
            val items = onRequest(currentKey)
            val newKey = getNextKey(currentKey, items)
            endReached = isEnd(items)
            onSuccess(items, newKey, endReached)
            currentKey = newKey
        } catch (t: Throwable) {
            onError(t)
        } finally {
            onLoadUpdated(false)
            mutex.withLock { isMakingRequest = false }
        }
    }

    override suspend fun reset() {
        mutex.withLock {
            currentKey = initialKey
            endReached = false
            isMakingRequest = false
        }
    }
}

data class PagingState<T>(
    val page: Int = 1,
    val isLoading: Boolean = false,
    val items: List<T> = emptyList(),
    val endReached: Boolean = false,
    val error: String? = null,
)
