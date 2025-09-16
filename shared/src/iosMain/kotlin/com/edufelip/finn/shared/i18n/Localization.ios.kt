package com.edufelip.finn.shared.i18n

import platform.Foundation.NSLocale

actual fun currentLanguage(): String {
    val code = NSLocale.currentLocale.languageCode
    return code ?: "en"
}
