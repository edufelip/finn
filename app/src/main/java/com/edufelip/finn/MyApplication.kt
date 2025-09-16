package com.edufelip.finn

import android.app.Application
import com.edufelip.finn.notifications.NotificationUtils
import com.edufelip.finn.shared.i18n.LanguageStore
import dagger.hilt.android.HiltAndroidApp
import org.koin.core.context.startKoin

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationUtils.initChannels(this)
        LanguageStore.init(this)
        // Start Koin DI for shared UI (modules are loaded at runtime per-Activity)
        startKoin { }
    }
}
