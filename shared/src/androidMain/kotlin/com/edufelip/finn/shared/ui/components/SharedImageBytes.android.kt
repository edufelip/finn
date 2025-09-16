package com.edufelip.finn.shared.ui.components

import androidx.compose.runtime.Composable
import coil.compose.AsyncImage

@Composable
actual fun SharedImageBytes(bytes: ByteArray, contentDescription: String?) {
    AsyncImage(model = bytes, contentDescription = contentDescription)
}
