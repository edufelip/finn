package com.edufelip.finn.ui.compose

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun ComponentActivity.pickImageFlow(): Flow<ByteArray?> = callbackFlow {
    val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            try {
                val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                trySend(bytes)
            } catch (e: Exception) {
                trySend(null)
            }
        } else {
            trySend(null)
        }
        close()
    }
    launcher.launch("image/*")
    awaitClose { }
}
