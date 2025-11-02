package com.edufelip.finn.shared.i18n

private var inMemoryLanguageCode: String? = null

actual fun readPersistedLanguageCode(): String? = inMemoryLanguageCode

actual fun persistLanguageCode(code: String?) {
    inMemoryLanguageCode = code
}
