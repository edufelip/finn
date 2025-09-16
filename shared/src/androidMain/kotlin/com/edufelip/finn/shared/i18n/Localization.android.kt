package com.edufelip.finn.shared.i18n

actual fun currentLanguage(): String = java.util.Locale.getDefault().language ?: "en"
