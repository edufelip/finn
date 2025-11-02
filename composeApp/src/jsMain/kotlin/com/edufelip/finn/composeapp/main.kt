package com.edufelip.finn.composeapp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.edufelip.finn.composeapp.di.composePlatformModule
import com.edufelip.finn.composeapp.di.stubPlatformBindings
import com.edufelip.finn.shared.di.DI
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    if (GlobalContext.getOrNull() == null) {
        val koinApp = startKoin {
            modules(composePlatformModule(stubPlatformBindings()))
        }
        DI.configure { koinApp.koin }
    }

    ComposeViewport {
        FinnApp()
    }
}
