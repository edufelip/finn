package com.edufelip.finn.shared.i18n

import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages

actual fun currentLanguage(): String {
    val languages = NSLocale.preferredLanguages ?: emptyList<Any>()
    val first = languages.firstOrNull() as? String
    val code = first?.substringBefore('-')
    return if (code.isNullOrBlank()) "en" else code
}
