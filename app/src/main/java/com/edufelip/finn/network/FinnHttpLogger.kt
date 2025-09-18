package com.edufelip.finn.network

import android.util.Log
import okhttp3.logging.HttpLoggingInterceptor

/** Logs OkHttp traffic to Logcat with a stable tag, splitting long lines when necessary. */
object FinnHttpLogger : HttpLoggingInterceptor.Logger {
    private const val TAG = "FinnNetwork"
    private const val MAX_LENGTH = 4000

    override fun log(message: String) {
        if (message.length <= MAX_LENGTH) {
            Log.d(TAG, message)
            return
        }
        var start = 0
        val length = message.length
        while (start < length) {
            val end = (start + MAX_LENGTH).coerceAtMost(length)
            Log.d(TAG, message.substring(start, end))
            start = end
        }
    }
}
