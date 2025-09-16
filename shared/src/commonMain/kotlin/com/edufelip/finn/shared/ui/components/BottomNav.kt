package com.edufelip.finn.shared.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.navigation.Route

@Composable
fun SharedBottomBar(current: Route, onNavigate: (Route) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = current is Route.Home,
            onClick = { onNavigate(Route.Home) },
            label = { Text(LocalStrings.current.home) },
            icon = {},
        )
        NavigationBarItem(
            selected = current is Route.Search,
            onClick = { onNavigate(Route.Search) },
            label = { Text(LocalStrings.current.search) },
            icon = {},
        )
        NavigationBarItem(
            selected = current is Route.Notifications,
            onClick = { onNavigate(Route.Notifications) },
            label = { Text(LocalStrings.current.alerts) },
            icon = {},
        )
        NavigationBarItem(
            selected = current is Route.Profile || current is Route.Saved,
            onClick = { onNavigate(Route.Profile) },
            label = { Text(LocalStrings.current.profile) },
            icon = {},
        )
    }
}
