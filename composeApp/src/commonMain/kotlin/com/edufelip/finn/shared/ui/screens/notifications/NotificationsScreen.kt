package com.edufelip.finn.shared.ui.screens.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.usecase.ObserveNotificationsUseCase
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.readableMessage
import com.edufelip.finn.shared.notifications.NotificationItem
import com.edufelip.finn.shared.notifications.NotificationsFacade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun NotificationsScreen(
    observe: ObserveNotificationsUseCase? = null,
    eventsFlow: Flow<NotificationItem> = NotificationsFacade.observe(),
) {
    var state by remember { mutableStateOf(NotificationsState()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(observe) {
        observe?.let { useCase ->
            scope.launch {
                useCase().collect { result ->
                    when (result) {
                        is Result.Success -> state = state.copy(
                            loading = false,
                            error = null,
                            items = listOf(result.value) + state.items.take(49),
                        )

                        is Result.Error -> state = state.copy(loading = false, error = result.error.readableMessage())
                        Result.Loading -> state = state.copy(loading = true)
                    }
                }
            }
        } ?: run {
            state = NotificationsState(items = List(5) { i -> NotificationItem(i, "Alert $i", "Description $i") })
        }

        scope.launch {
            eventsFlow.collect { item ->
                state = state.copy(items = listOf(item) + state.items.take(49))
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        LazyColumn(Modifier.fillMaxSize()) {
            items(state.items) { notification ->
                Column(Modifier.padding(vertical = 8.dp)) {
                    Text(notification.title, style = MaterialTheme.typography.titleMedium)
                    Text(notification.body, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
