package com.edufelip.finn

import android.app.Application
import com.edufelip.finn.notifications.NotificationUtils
import com.edufelip.finn.shared.i18n.LanguageStore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationUtils.initChannels(this)
        LanguageStore.init(this)
    }
}
