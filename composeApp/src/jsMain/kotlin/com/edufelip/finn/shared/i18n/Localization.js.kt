package com.edufelip.finn.shared.i18n

import kotlinx.browser.window

actual fun currentLanguage(): String = window.navigator.language
