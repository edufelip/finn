package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.repository.NotificationsRepository
import com.edufelip.finn.shared.presentation.notifications.NotificationItem
import kotlinx.coroutines.flow.Flow

class ObserveNotificationsUseCase(private val repo: NotificationsRepository) {
    operator fun invoke(): Flow<NotificationItem> = repo.observe()
}
