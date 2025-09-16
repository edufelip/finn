package com.edufelip.finn.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.edufelip.finn.notifications.NotificationsRepositoryAndroid
import com.edufelip.finn.shared.domain.usecase.ObserveNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    @ApplicationContext context: Context,
) : ViewModel() {
    val observeNotifications: ObserveNotificationsUseCase? = ObserveNotificationsUseCase(NotificationsRepositoryAndroid(context))
}
