package com.edufelip.finn.shared.i18n

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages

@OptIn(ExperimentalForeignApi::class)
actual fun currentLanguage(): String {
    val languages = NSLocale.preferredLanguages ?: emptyList<Any>()
    val first = languages.firstOrNull() as? String
    val code = first?.substringBefore('-')
    return if (code.isNullOrBlank()) "en" else code
}
