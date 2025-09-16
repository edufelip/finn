package com.edufelip.finn.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.text.TextUtils
import android.util.Patterns

object Verify {
    fun isEmailValid(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    @Suppress("DEPRECATION")
    fun isInternetOn(context: Context): Boolean {
        return try {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val networkInfo: NetworkInfo? = manager?.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        } catch (_: NullPointerException) {
            false
        }
    }
}
