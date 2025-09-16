package com.edufelip.finn.shared.i18n

import android.content.Context

object LanguageStore {
    @Volatile private var appContext: Context? = null
    fun init(context: Context) {
        appContext = context.applicationContext
    }
    internal fun prefs() = appContext?.getSharedPreferences("finn_prefs", Context.MODE_PRIVATE)
}

private const val KEY_LANG = "lang_code"

actual fun readPersistedLanguageCode(): String? = LanguageStore.prefs()?.getString(KEY_LANG, null)

actual fun persistLanguageCode(code: String?) {
    LanguageStore.prefs()?.edit()?.apply {
        if (code == null) remove(KEY_LANG) else putString(KEY_LANG, code)
    }?.apply()
}
