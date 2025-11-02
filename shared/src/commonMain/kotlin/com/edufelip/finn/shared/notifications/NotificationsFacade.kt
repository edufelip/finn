package com.edufelip.finn.shared.notifications

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

object NotificationsFacade {
    private val _events = MutableSharedFlow<NotificationItem>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    fun observe(): Flow<NotificationItem> = _events
    suspend fun emit(item: NotificationItem) {
        _events.emit(item)
    }
}
