package com.edufelip.finn.shared.navigation

sealed interface Route {
    data object Login : Route
    data object Home : Route
    data object Search : Route
    data class CommunityDetails(val id: Int) : Route
    data class PostDetails(val id: Int) : Route
    data object Notifications : Route
    data object Profile : Route
    data object CreateCommunity : Route
    data object Saved : Route
    data object CreatePost : Route
    data object Settings : Route
}
