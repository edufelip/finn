package com.edufelip.finn.utils.extensions

import android.content.Context
import android.widget.Toast

fun Context.shortToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}