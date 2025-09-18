package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.domain.repository.NotificationsRepository
import com.edufelip.finn.shared.domain.util.DomainError
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.retryWithBackoff
import com.edufelip.finn.shared.presentation.notifications.NotificationItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ObserveNotificationsUseCase(private val repo: NotificationsRepository) {
    operator fun invoke(): Flow<Result<NotificationItem>> =
        repo.observe()
            .map<NotificationItem, Result<NotificationItem>> { Result.Success(it) }
            .onStart { emit(Result.Loading) }
            .retryWithBackoff(maxRetries = 3, initialDelay = 200L) { true }
            .catch { emit(Result.Error(DomainError.Network(it.message, isTransient = true))) }
}
