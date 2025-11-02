package com.edufelip.finn.shared.i18n

import java.util.Locale

actual fun currentLanguage(): String = Locale.getDefault().language
