package com.edufelip.finn.shared.domain.repository

import com.edufelip.finn.shared.presentation.notifications.NotificationItem
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {
    fun observe(): Flow<NotificationItem>
}
