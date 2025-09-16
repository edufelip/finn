package com.edufelip.finn.shared.i18n

import platform.Foundation.NSUserDefaults

private const val KEY_LANG = "lang_code"

actual fun readPersistedLanguageCode(): String? = NSUserDefaults.standardUserDefaults.stringForKey(KEY_LANG)

actual fun persistLanguageCode(code: String?) {
    val defs = NSUserDefaults.standardUserDefaults
    if (code == null) defs.removeObjectForKey(KEY_LANG) else defs.setObject(code, KEY_LANG)
}
