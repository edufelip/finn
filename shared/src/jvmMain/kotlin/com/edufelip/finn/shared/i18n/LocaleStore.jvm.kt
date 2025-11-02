package com.edufelip.finn.shared.i18n

import java.util.prefs.Preferences

private val preferences: Preferences by lazy {
    Preferences.userRoot().node("com.edufelip.finn.shared")
}

actual fun readPersistedLanguageCode(): String? = preferences.get("language_code", null)

actual fun persistLanguageCode(code: String?) {
    if (code == null) {
        preferences.remove("language_code")
    } else {
        preferences.put("language_code", code)
    }
}
