package com.edufelip.finn.shared.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.navigation.Route

@Composable
fun SharedBottomBar(
    current: Route,
    onNavigate: (Route) -> Unit,
    onCreateClick: () -> Unit,
) {
    val strings = LocalStrings.current
    NavigationBar {
        NavigationBarItem(
            selected = current is Route.Home,
            onClick = { onNavigate(Route.Home) },
            label = { Text(strings.home) },
            icon = { androidx.compose.material3.Icon(Icons.Filled.Home, contentDescription = strings.home) },
        )
        NavigationBarItem(
            selected = false,
            onClick = onCreateClick,
            label = { Text(strings.create) },
            icon = { androidx.compose.material3.Icon(Icons.Filled.AddCircle, contentDescription = strings.create) },
        )
        NavigationBarItem(
            selected = current is Route.Search,
            onClick = { onNavigate(Route.Search) },
            label = { Text(strings.search) },
            icon = { androidx.compose.material3.Icon(Icons.Filled.Search, contentDescription = strings.search) },
        )
        NavigationBarItem(
            selected = current is Route.Notifications,
            onClick = { onNavigate(Route.Notifications) },
            label = { Text(strings.alerts) },
            icon = { androidx.compose.material3.Icon(Icons.Filled.Notifications, contentDescription = strings.alerts) },
        )
    }
}
