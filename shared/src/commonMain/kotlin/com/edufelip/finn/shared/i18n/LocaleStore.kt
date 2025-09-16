package com.edufelip.finn.shared.i18n

expect fun readPersistedLanguageCode(): String?
expect fun persistLanguageCode(code: String?)
