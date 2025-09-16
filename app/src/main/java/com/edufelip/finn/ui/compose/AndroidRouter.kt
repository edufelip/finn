package com.edufelip.finn.ui.compose

import com.edufelip.finn.shared.navigation.Route
import com.edufelip.finn.shared.navigation.Router
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AndroidRouter(start: Route = Route.Home) : Router {
    private val stack = ArrayDeque<Route>().apply { add(start) }
    private val _current = MutableStateFlow(start)
    override val current: StateFlow<Route> = _current

    val canGoBack: Boolean get() = stack.size > 1

    override fun navigate(to: Route) {
        stack.addLast(to)
        _current.value = to
    }

    override fun back() {
        if (stack.size > 1) {
            stack.removeLast()
            _current.value = stack.last()
        }
    }

    fun snapshotPaths(): List<String> = stack.map { routeAsPath(it) }

    fun restorePaths(paths: List<String>) {
        if (paths.isEmpty()) return
        val routes = paths.mapNotNull { parseRoute(it) }
        if (routes.isNotEmpty()) {
            stack.clear()
            stack.addAll(routes)
            _current.value = stack.last()
        }
    }
}

fun routeAsPath(route: Route): String = when (route) {
    Route.Home -> "home"
    Route.Login -> "login"
    Route.Search -> "search"
    is Route.CommunityDetails -> "community/${(route as Route.CommunityDetails).id}"
    is Route.PostDetails -> "post/${(route as Route.PostDetails).id}"
    Route.Notifications -> "notifications"
    Route.Profile -> "profile"
    Route.CreateCommunity -> "createCommunity"
    Route.Saved -> "saved"
    Route.CreatePost -> "createPost"
    Route.Settings -> "settings"
}

fun parseRoute(path: String): Route? {
    val clean = path.trim().removePrefix("/")
    return when {
        clean == "home" -> Route.Home
        clean == "login" -> Route.Login
        clean == "search" -> Route.Search
        clean.startsWith("community/") -> clean.substringAfter("community/").toIntOrNull()?.let { Route.CommunityDetails(it) }
        clean.startsWith("post/") -> clean.substringAfter("post/").toIntOrNull()?.let { Route.PostDetails(it) }
        clean == "notifications" -> Route.Notifications
        clean == "profile" -> Route.Profile
        clean == "createCommunity" -> Route.CreateCommunity
        clean == "saved" -> Route.Saved
        clean == "createPost" -> Route.CreatePost
        clean == "settings" -> Route.Settings
        else -> null
    }
}
