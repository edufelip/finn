package com.edufelip.finn.shared.presentation.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.usecase.ObserveNotificationsUseCase
import com.edufelip.finn.shared.notifications.NotificationsFacade
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun NotificationsScreen(observe: ObserveNotificationsUseCase? = null) {
    var state by remember { mutableStateOf(NotificationsState()) }
    val scope = remember { CoroutineScope(Dispatchers.Main) }
    LaunchedEffect(Unit) {
        observe?.let { useCase ->
            scope.launch { useCase().collect { item -> state = state.copy(items = listOf(item) + state.items.take(49)) } }
        }
        scope.launch { NotificationsFacade.observe().collect { item -> state = state.copy(items = listOf(item) + state.items.take(49)) } }
        if (observe == null) {
            state = NotificationsState(items = List(5) { i -> NotificationItem(i, "Alert ${'$'}i", "Description ${'$'}i") })
        }
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(state.items) { n ->
                Column(Modifier.padding(vertical = 8.dp)) {
                    Text(n.title)
                    Text(n.body)
                }
            }
        }
    }
}
