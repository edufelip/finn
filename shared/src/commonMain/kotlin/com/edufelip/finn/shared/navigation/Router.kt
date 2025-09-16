package com.edufelip.finn.shared.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface Router {
    val current: StateFlow<Route>
    fun navigate(to: Route)
    fun back()
}

class SimpleRouter(initial: Route = Route.Home) : Router {
    private val stack = ArrayDeque<Route>().apply { add(initial) }
    private val _current = MutableStateFlow(initial)
    override val current: StateFlow<Route> = _current

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
}
