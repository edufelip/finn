package com.edufelip.finn.shared.ui.components.molecules

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.navigation.Route

@Composable
fun AppBottomBar(
    currentRoute: Route,
    onNavigate: (Route) -> Unit,
    onCreateClick: () -> Unit,
) {
    val strings = LocalStrings.current
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute is Route.Home,
            onClick = { onNavigate(Route.Home) },
            label = { Text(strings.home) },
            icon = { Icon(Icons.Filled.Home, contentDescription = strings.home) },
        )
        NavigationBarItem(
            selected = false,
            onClick = onCreateClick,
            label = { Text(strings.create) },
            icon = { Icon(Icons.Filled.AddCircle, contentDescription = strings.create) },
        )
        NavigationBarItem(
            selected = currentRoute is Route.Search,
            onClick = { onNavigate(Route.Search) },
            label = { Text(strings.search) },
            icon = { Icon(Icons.Filled.Search, contentDescription = strings.search) },
        )
        NavigationBarItem(
            selected = currentRoute is Route.Notifications,
            onClick = { onNavigate(Route.Notifications) },
            label = { Text(strings.alerts) },
            icon = { Icon(Icons.Filled.Notifications, contentDescription = strings.alerts) },
        )
    }
}
