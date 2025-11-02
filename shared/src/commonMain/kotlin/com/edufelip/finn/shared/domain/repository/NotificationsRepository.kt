package com.edufelip.finn.shared.domain.repository

import com.edufelip.finn.shared.notifications.NotificationItem
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {
    fun observe(): Flow<NotificationItem>
}
