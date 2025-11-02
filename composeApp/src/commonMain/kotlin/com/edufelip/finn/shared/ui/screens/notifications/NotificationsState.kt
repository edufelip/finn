package com.edufelip.finn.shared.ui.screens.notifications

import com.edufelip.finn.shared.notifications.NotificationItem

data class NotificationsState(
    val loading: Boolean = false,
    val items: List<NotificationItem> = emptyList(),
    val error: String? = null,
)
