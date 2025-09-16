package com.edufelip.finn.shared.presentation.notifications

data class NotificationItem(val id: Int, val title: String, val body: String)

data class NotificationsState(
    val loading: Boolean = false,
    val items: List<NotificationItem> = emptyList(),
    val error: String? = null,
)
