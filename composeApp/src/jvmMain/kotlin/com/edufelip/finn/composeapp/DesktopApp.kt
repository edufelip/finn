package com.edufelip.finn.composeapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.edufelip.finn.composeapp.di.composePlatformModule
import com.edufelip.finn.composeapp.di.stubPlatformBindings
import com.edufelip.finn.shared.di.DI
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

fun main() = application {
    if (GlobalContext.getOrNull() == null) {
        val koinApp = startKoin {
            modules(composePlatformModule(stubPlatformBindings()))
        }
        DI.configure { koinApp.koin }
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Finn",
    ) {
        FinnApp()
    }
}
