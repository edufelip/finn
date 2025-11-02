package com.edufelip.finn.composeapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.edufelip.finn.shared.navigation.Router
import com.edufelip.finn.shared.navigation.SimpleRouter
import com.edufelip.finn.shared.ui.screens.app.SharedApp

@Composable
fun FinnApp(router: Router) {
    SharedApp(router = router)
}

@Composable
fun FinnApp() {
    val router = remember { SimpleRouter() }
    FinnApp(router = router)
}
