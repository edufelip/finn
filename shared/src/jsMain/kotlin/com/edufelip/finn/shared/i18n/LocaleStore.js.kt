package com.edufelip.finn.shared.i18n

import kotlinx.browser.window

private const val LANGUAGE_CODE_KEY = "finn_language_code"

actual fun readPersistedLanguageCode(): String? =
    runCatching { window.localStorage.getItem(LANGUAGE_CODE_KEY) }.getOrNull()

actual fun persistLanguageCode(code: String?) {
    runCatching {
        val storage = window.localStorage
        if (code.isNullOrBlank()) {
            storage.removeItem(LANGUAGE_CODE_KEY)
        } else {
            storage.setItem(LANGUAGE_CODE_KEY, code)
        }
    }
}
